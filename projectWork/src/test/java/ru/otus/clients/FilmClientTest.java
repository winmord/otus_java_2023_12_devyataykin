package ru.otus.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.dto.FilmDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmClientTest {
    private FilmClient filmClient;

    @BeforeEach
    public void setUp() {
        filmClient = new FilmClient(System.getenv("KINOPOISK_API_KEY"), System.getenv("KINOPOISK_API_HOST"));
    }

    @Test
    void shouldFindFilmByName() {
        List<FilmDto> films = filmClient.findByKeyword("Matrix");

        assertNotNull(films);
        assertTrue(films.size() > 3);
    }

    @Test
    void shouldReturnEmptyFilmList() {
        List<FilmDto> films = filmClient.findByKeyword(".");

        assertNotNull(films);
        assertTrue(films.isEmpty());
    }
}
