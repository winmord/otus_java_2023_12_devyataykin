package ru.otus.dto;

import java.util.List;

public record SearchDto(
        List<FilmDto> films
) {
}
