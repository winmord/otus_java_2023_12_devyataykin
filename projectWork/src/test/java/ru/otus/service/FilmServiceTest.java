package ru.otus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.clients.FilmClient;
import ru.otus.dto.FilmDto;
import ru.otus.repository.FavouriteRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class FilmServiceTest {
    private static final String FILM_ENGLISH_NAME = "Matrix";
    private static final String FILM_RUSSIAN_NAME = "Матрица";

    List<FilmDto> filmDtos = List.of(
            new FilmDto(301L, "Матрица", "The Matrix", "FILM", "1999", "", "02:16", "8.5", "", new ArrayList<>(), new ArrayList<>()),
            new FilmDto(299L, "Матрица: Перезагрузка", "The Matrix Reloaded", "FILM", "2003", "", "02:18", "7.7", "", new ArrayList<>(), new ArrayList<>()),
            new FilmDto(1294123L, "Матрица: Воскрешение", "The Matrix Resurrections", "FILM", "2021", "", "02:28", "5.7", "", new ArrayList<>(), new ArrayList<>()),
            new FilmDto(316L, "Матрица: Революция", "The Matrix Revolutions", "FILM", "2003", "", "02:09", "7.6", "", new ArrayList<>(), new ArrayList<>())
    );
    private FilmClient filmClient;
    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        filmClient = mock(FilmClient.class);
        FavouriteRepository favouriteRepository = mock(FavouriteRepository.class);
        filmService = new FilmService(filmClient, favouriteRepository);
    }

    @Test
    void shouldFindFilmByNameInEnglish() {
        given(filmClient.findByKeyword(anyString())).willReturn(filmDtos);

        String searchResult = filmService.searchFilm(FILM_ENGLISH_NAME);
        assertNotNull(searchResult);
        assertTrue(searchResult.contains("Перезагрузка"));
        assertTrue(searchResult.contains("Революция"));
        assertTrue(searchResult.split(System.lineSeparator()).length > 3);
    }

    @Test
    void shouldFindFilmByNameInRussian() {
        given(filmClient.findByKeyword(anyString())).willReturn(filmDtos);

        String searchResult = filmService.searchFilm(FILM_RUSSIAN_NAME);
        assertNotNull(searchResult);
        assertTrue(searchResult.contains("Перезагрузка"));
        assertTrue(searchResult.contains("Революция"));
        assertTrue(searchResult.split(System.lineSeparator()).length > 3);
    }

    @Test
    void shouldDisplayFilmNotFound() {
        given(filmClient.findByKeyword(anyString())).willReturn(new ArrayList<>());

        String keyword = ".";
        String searchResult = filmService.searchFilm(keyword);
        assertNotNull(searchResult);
        assertEquals("По запросу \"" + keyword + "\" ничего не найдено.\r\n", searchResult);
    }
}