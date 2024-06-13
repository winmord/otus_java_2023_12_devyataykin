package ru.otus.dto;

import java.util.List;

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
        List<CountryDto> countries
) {
}
