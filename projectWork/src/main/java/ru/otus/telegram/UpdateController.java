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
import ru.otus.model.Favourite;
import ru.otus.repository.FavouriteRepository;

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
    private final Map<String, Function<Update, String>> handlers = Map.of(
            "/start", (update) -> "Отправьте название фильма, который желаете найти",
            "/add_favourite", this::addToFavourite,
            "/favourite", this::showFavourites,
            "Избранное 🔖", this::showFavourites,
            "Помощь 🆘", (update) -> "Для навигации воспользуйтесь кнопками меню. Чтобы найти фильм, просто отправьте его название.",
            "/default", this::searchFilm
    );

    private final FavouriteRepository favouriteRepository;

    public UpdateController(FilmClient filmClient, InlineKeyboardMaker inlineKeyboardMaker, ReplyKeyboardMaker replyKeyboardMaker, FavouriteRepository favouriteRepository) {
        this.filmClient = filmClient;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        this.replyKeyboardMaker = replyKeyboardMaker;
        this.favouriteRepository = favouriteRepository;
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

        Function<Update, String> handler = handlers.getOrDefault(requestMessage, defaultHandler(update));
        String responseMessageText = handler.apply(update);

        SendMessage sendMessage = prepareMessage(requestMessage, chatId, responseMessageText);
        sendMessage(sendMessage);
    }

    private void processCallbackQuery(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Function<Update, String> handler = handlers.getOrDefault("/add_favourite", defaultHandler(update));
        String responseMessageText = handler.apply(update);

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
            String buttonText = favouriteRepository.findByChatIdAndFilmId(chatId, requestMessage.substring(1)).isPresent()
                    ? "Удалить из избранного"
                    : "Добавить в избранное";
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

    private Function<Update, String> defaultHandler(Update update) {
        String requestMessage = update.getMessage() != null ? update.getMessage().getText() : update.getCallbackQuery().getData();
        if (isFilmDetailsMessage(requestMessage)) {
            return this::getFilm;
        }

        return this::searchFilm;
    }

    private String searchFilm(Update update) {
        String requestMessage = update.getMessage().getText();
        List<FilmDto> films = filmClient.search(requestMessage);
        StringBuilder builder = new StringBuilder();

        if (films.isEmpty()) {
            builder.append("По запросу \"").append(requestMessage).append("\" ничего не найдено.").append(System.lineSeparator());
            return builder.toString();
        }

        builder.append("По запросу \"").append(requestMessage).append("\" найдено:").append(System.lineSeparator());

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

    private String getFilm(Update update) {
        String filmId = update.getMessage().getText().substring(1);
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

    private String addToFavourite(Update update) {
        String requestMessage = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        String filmId = requestMessage.substring(1);
        FilmDto film = filmClient.find(filmId);

        Optional<Favourite> favouriteFromRepository = favouriteRepository.findByChatIdAndFilmId(chatId, filmId);
        if (favouriteFromRepository.isPresent()) {
            favouriteRepository.delete(favouriteFromRepository.get());
            return favouriteFromRepository.get().getFilmName() + " (" + favouriteFromRepository.get().getFilmYear() + ") удалён из избранного!";
        }

        String filmName = film.nameRu() != null && !film.nameRu().isEmpty() && !film.nameRu().equals("null") ? film.nameRu() : film.nameEn();
        String filmYear = film.year();
        String filmShortDescription = filmName + " (" + filmYear + ")";
        favouriteRepository.save(new Favourite(null, chatId, filmName, filmYear, filmId));

        return filmShortDescription + " добавлен в избранное!";
    }

    private String showFavourites(Update update) {
        Long chatId = update.getMessage().getChatId();
        Collection<Favourite> favourites = favouriteRepository.findAllByChatId(chatId);

        if (favourites.isEmpty()) {
            return "Вы ещё ничего не добоавляли в избранное";
        }

        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (Favourite favourite : favourites) {
            builder
                    .append(++counter)
                    .append(". ")
                    .append(favourite.getFilmName())
                    .append(" (").append(favourite.getFilmYear()).append(")")
                    .append(" [/").append(favourite.getFilmId()).append("]")
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }
}
