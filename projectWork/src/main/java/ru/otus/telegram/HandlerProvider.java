package ru.otus.telegram;

import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.service.FilmService;

@Service
public class HandlerProvider {
    private final FilmService filmService;

    public HandlerProvider(FilmService filmService) {
        this.filmService = filmService;
    }

    private final Map<String, Function<Update, String>> handlers = Map.of(
            "/start",
            update -> "Отправьте название фильма, который желаете найти",
            "/add_favourite",
            this::changeFavouriteStateHandler,
            "/favourite",
            this::showFavouritesHandler,
            "Избранное 🔖",
            this::showFavouritesHandler,
            "Помощь 🆘",
            update -> "Для навигации воспользуйтесь кнопками меню. Чтобы найти фильм, просто отправьте его название.",
            "/is_favourite",
            this::isFilmFavouriteHandler,
            "/default",
            this::searchFilmHandler);

    public Function<Update, String> getHandler(String requestMessage, Update update) {
        return handlers.getOrDefault(requestMessage, defaultHandler(update));
    }

    private Function<Update, String> defaultHandler(Update update) {
        String requestMessage = update.hasMessage()
                ? update.getMessage().getText()
                : update.getCallbackQuery().getData();
        if (filmService.isFilmDetailsMessage(requestMessage)) {
            return this::getFilmHandler;
        }

        return this::searchFilmHandler;
    }

    private String searchFilmHandler(Update update) {
        String requestMessage = update.getMessage().getText();
        return filmService.searchFilm(requestMessage);
    }

    private String getFilmHandler(Update update) {
        String requestMessage = update.getMessage().getText();
        String filmId = getFilmIdFromMessage(requestMessage);
        return filmService.getFilm(filmId);
    }

    private String changeFavouriteStateHandler(Update update) {
        String requestMessage = update.getCallbackQuery().getData();
        String filmId = getFilmIdFromMessage(requestMessage);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        return filmService.changeFavouriteState(filmId, chatId);
    }

    private String showFavouritesHandler(Update update) {
        Long chatId = update.getMessage().getChatId();
        return filmService.showFavourites(chatId);
    }

    private String isFilmFavouriteHandler(Update update) {
        Long chatId = update.getMessage().getChatId();
        String requestMessage = update.getMessage().getText();
        String filmId = getFilmIdFromMessage(requestMessage);

        return filmService.isFilmFavourite(chatId, filmId) ? "favourite" : null;
    }

    private String getFilmIdFromMessage(String message) {
        return message.substring(1);
    }
}
