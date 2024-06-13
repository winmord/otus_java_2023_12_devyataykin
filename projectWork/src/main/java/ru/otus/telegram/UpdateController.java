package ru.otus.telegram;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.otus.clients.FilmClient;
import ru.otus.dto.CountryDto;
import ru.otus.dto.FilmDto;
import ru.otus.dto.GenreDto;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private final FilmClient filmClient;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    private final ReplyKeyboardMaker replyKeyboardMaker;
    private final Map<String, Function<String, String>> handlers = Map.of(
            "/start", (String) -> "Отправьте название фильма, который желаете найти",
            "/add_favourite", this::addToFavourite,
            "/favourite", this::showFavourites,
            "Избранное 🔖", this::showFavourites,
            "Помощь 🆘", (String) -> "Для навигации воспользуйтесь кнопками меню. Чтобы найти фильм, просто отправьте его название.",
            "/default", this::searchFilm
    );
    private final Map<String, String> favourites = new HashMap<>();

    public UpdateController(FilmClient filmClient, InlineKeyboardMaker inlineKeyboardMaker, ReplyKeyboardMaker replyKeyboardMaker) {
        this.filmClient = filmClient;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        this.replyKeyboardMaker = replyKeyboardMaker;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;

        log.info("register bot");
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public void processUpdate(Update update) {
        if (update.hasMessage()) {
            processMessage(update);
        } else if (update.hasCallbackQuery()) {
            processCallbackQuery(update);
        }
    }

    private void processMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String requestMessage = update.getMessage().getText();

        Function<String, String> handler = handlers.getOrDefault(requestMessage, defaultHandler(requestMessage));
        String responseMessageText = handler.apply(requestMessage);

        SendMessage sendMessage = prepareMessage(requestMessage, chatId, responseMessageText);
        sendMessage(sendMessage);
    }

    private void processCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String requestMessage = update.getCallbackQuery().getData();

        Function<String, String> handler = handlers.getOrDefault("/add_favourite", defaultHandler(requestMessage));
        String responseMessageText = handler.apply(requestMessage);

        SendMessage sendMessage = prepareMessage("", chatId, responseMessageText);
        sendMessage(sendMessage);
    }

    private boolean isFilmDetailsMessage(String messageText) {
        String regex = "^/\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageText);

        return matcher.find();
    }

    private SendMessage prepareMessage(String requestMessage, Long chatId, String messageText) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), messageText);

        if (isFilmDetailsMessage(requestMessage)) {
            String buttonText = favourites.containsKey(requestMessage) ? "Удалить из избранного" : "Добавить в избранное";
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardMaker.getInlineMessageButtons(buttonText, requestMessage);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        } else if ("/start".equals(requestMessage)) {
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        }

        return sendMessage;
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private Function<String, String> defaultHandler(String messageText) {
        if (isFilmDetailsMessage(messageText)) {
            return this::getFilm;
        }

        return this::searchFilm;
    }

    private String searchFilm(String keyword) {
        List<FilmDto> films = filmClient.search(keyword);
        StringBuilder builder = new StringBuilder();

        if (films.isEmpty()) {
            builder.append("По запросу \"").append(keyword).append("\" ничего не найдено.").append(System.lineSeparator());
            return builder.toString();
        }

        builder.append("По запросу \"").append(keyword).append("\" найдено:").append(System.lineSeparator());

        int counter = 0;
        for (FilmDto film : films) {
            builder.append(++counter).append(". ")
                    .append(film.type().equals("FILM") ? "📽" : "📺")
                    .append(film.nameRu() == null ? film.nameEn() : film.nameRu())
                    .append(" (").append(film.year()).append(") ")
                    .append(film.rating() != null && !film.rating().equals("null") ? "⭐️" + film.rating() : "")
                    .append(" [/").append(film.filmId()).append("]")
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String getFilm(String keyword) {
        String filmId = keyword.substring(1);
        FilmDto film = filmClient.find(filmId);

        if (film == null) {
            return "Фильм " + filmId + " не найден.";
        }

        String name = film.nameRu() != null && !film.nameRu().isEmpty() && !film.nameRu().equals("null") ? film.nameRu() : film.nameEn();
        String rating = film.rating() != null && !film.rating().equals("null") ? film.rating() : "-";
        String filmLength = (film.filmLength() != null && !film.filmLength().equals("null") ? film.filmLength() : "-");
        String genres = film.genres() == null || film.genres().isEmpty() ? "-" : film.genres().stream().map(GenreDto::genre).collect(Collectors.joining(","));
        String countries = film.countries() == null || film.countries().isEmpty() ? "-" : film.countries().stream().map(CountryDto::country).collect(Collectors.joining(","));

        StringBuilder builder = new StringBuilder();
        builder.append(name).append(" (").append(film.year()).append(")").append(System.lineSeparator())
                .append("⭐️ ").append(rating).append(System.lineSeparator())
                .append("⏳ ").append(filmLength).append(" ч.").append(System.lineSeparator())
                .append("🎭 ").append(genres).append(System.lineSeparator())
                .append("📍 ").append(countries).append(System.lineSeparator())
                .append("📃 ").append(film.description() == null || film.description().isEmpty() ? "-" : film.description())
                .append(System.lineSeparator()).append(System.lineSeparator())
                .append(film.posterUrl());

        return builder.toString();
    }

    private String addToFavourite(String requestMessage) {
        String filmId = requestMessage.substring(1);
        FilmDto film = filmClient.find(filmId);

        String name = film.nameRu() != null && !film.nameRu().isEmpty() && !film.nameRu().equals("null") ? film.nameRu() : film.nameEn();
        String filmShortDescription = name + " (" + film.year() + ")";

        if (favourites.containsKey(requestMessage)) {
            favourites.remove(requestMessage);
            return filmShortDescription + " удалён из избранного!";
        }

        favourites.put(requestMessage, filmShortDescription + " [" + requestMessage + "]");
        return filmShortDescription + " добавлен в избранное!";
    }

    private String showFavourites(String requestMessage) {
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (String film : favourites.values()) {
            builder.append(++counter).append(". ").append(film).append(System.lineSeparator());
        }

        return builder.toString();
    }
}
