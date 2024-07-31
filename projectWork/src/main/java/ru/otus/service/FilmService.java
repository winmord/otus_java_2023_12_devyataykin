package ru.otus.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import ru.otus.clients.FilmClient;
import ru.otus.dto.FilmDto;
import ru.otus.model.Favourite;
import ru.otus.repository.FavouriteRepository;

@Service
public class FilmService {
    private final FilmClient filmClient;
    private final FavouriteRepository favouriteRepository;

    public FilmService(FilmClient filmClient, FavouriteRepository favouriteRepository) {
        this.filmClient = filmClient;
        this.favouriteRepository = favouriteRepository;
    }

    public boolean isFilmDetailsMessage(String messageText) {
        String regex = "^/\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageText);
        return matcher.find();
    }

    public boolean isFilmFavourite(Long chatId, String filmId) {
        return favouriteRepository.findByChatIdAndFilmId(chatId, filmId).isPresent();
    }

    public String searchFilm(String requestMessage) {
        List<FilmDto> films = filmClient.search(requestMessage);
        StringBuilder builder = new StringBuilder();

        if (films.isEmpty()) {
            builder.append("По запросу \"")
                    .append(requestMessage)
                    .append("\" ничего не найдено.")
                    .append(System.lineSeparator());
            return builder.toString();
        }

        builder.append("По запросу \"")
                .append(requestMessage)
                .append("\" найдено:")
                .append(System.lineSeparator());

        int counter = 0;
        for (FilmDto film : films) {
            builder.append(++counter)
                    .append(". ")
                    .append(film.type().equals("FILM") ? "📽" : "📺")
                    .append(film.nameRu() == null ? film.nameEn() : film.nameRu())
                    .append(" (")
                    .append(film.year())
                    .append(") ")
                    .append(film.rating() != null && !film.rating().equals("null") ? "⭐️" + film.rating() : "")
                    .append(" [/")
                    .append(film.filmId())
                    .append("]")
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    public String getFilm(String filmId) {
        FilmDto film = filmClient.find(filmId);

        if (film == null) {
            return "Фильм " + filmId + " не найден.";
        }

        String name = film.getName();
        String rating = film.getRating();
        String filmLength = film.getLength();
        String genres = film.getGenresSummary();
        String countries = film.getCountriesSummary();

        StringBuilder builder = new StringBuilder();
        builder.append(name)
                .append(" (")
                .append(film.year())
                .append(")")
                .append(System.lineSeparator())
                .append("⭐️ ")
                .append(rating)
                .append(System.lineSeparator())
                .append("⏳ ")
                .append(filmLength)
                .append(" ч.")
                .append(System.lineSeparator())
                .append("🎭 ")
                .append(genres)
                .append(System.lineSeparator())
                .append("📍 ")
                .append(countries)
                .append(System.lineSeparator())
                .append("📃 ")
                .append(film.description() == null || film.description().isEmpty() ? "-" : film.description())
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(film.posterUrl());

        return builder.toString();
    }

    public String changeFavouriteState(String filmId, Long chatId) {
        FilmDto film = filmClient.find(filmId);

        Optional<Favourite> favouriteFromRepository = favouriteRepository.findByChatIdAndFilmId(chatId, filmId);
        if (favouriteFromRepository.isPresent()) {
            favouriteRepository.delete(favouriteFromRepository.get());
            return favouriteFromRepository.get().getFilmName() + " ("
                    + favouriteFromRepository.get().getFilmYear() + ") удалён из избранного!";
        }

        favouriteRepository.save(new Favourite(null, chatId, film.getName(), film.getYear(), filmId));

        return film.getNameYear() + " добавлен в избранное!";
    }

    public String showFavourites(Long chatId) {
        Collection<Favourite> favourites = favouriteRepository.findAllByChatId(chatId);

        if (favourites.isEmpty()) {
            return "Вы ещё ничего не добавляли в избранное";
        }

        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (Favourite favourite : favourites) {
            builder.append(++counter)
                    .append(". ")
                    .append(favourite.getFilmName())
                    .append(" (")
                    .append(favourite.getFilmYear())
                    .append(")")
                    .append(" [/")
                    .append(favourite.getFilmId())
                    .append("]")
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }
}
