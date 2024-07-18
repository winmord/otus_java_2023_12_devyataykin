package ru.otus.dto;

import java.util.List;
import java.util.stream.Collectors;

public record FilmDto(
        Long filmId,
        String nameRu,
        String nameEn,
        String type,
        String year,
        String description,
        String filmLength,
        String rating,
        String posterUrl,
        List<GenreDto> genres,
        List<CountryDto> countries) {
    public String getName() {
        return nameRu != null && !nameRu.isEmpty() && !nameRu.equals("null") ? nameRu : nameEn;
    }

    public String getYear() {
        if (year == null || year.isBlank()) {
            return "-";
        }
        return year;
    }

    public String getRating() {
        return rating != null && !rating.equals("null") ? rating : "-";
    }

    public String getLength() {
        return filmLength != null && !filmLength.equals("null") ? filmLength : "-";
    }

    public String getGenresSummary() {
        return genres == null || genres.isEmpty()
                ? "-"
                : genres.stream().map(GenreDto::genre).collect(Collectors.joining(","));
    }

    public String getCountriesSummary() {
        return countries == null || countries.isEmpty()
                ? "-"
                : countries.stream().map(CountryDto::country).collect(Collectors.joining(","));
    }

    public String getNameYear() {
        return getName() + " (" + getYear() + ")";
    }
}
