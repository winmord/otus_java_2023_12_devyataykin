package ru.otus.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.otus.dto.FilmData;
import ru.otus.dto.FilmDto;
import ru.otus.dto.SearchDto;

import java.util.*;

@Slf4j
@Component
public class FilmClient {
    private final String apikey;
    private final String HOST = "kinopoiskapiunofficial.tech";
    private final String APIKEY_HEADER_NAME = "X-API-KEY";
    private final Map<String, List<FilmDto>> searchCache = new WeakHashMap<>();
    private final Map<String, FilmDto> filmCache = new WeakHashMap<>();

    public FilmClient(@Value("${kinopoisk.api.key}") String apikey) {
        this.apikey = apikey;
    }

    public List<FilmDto> search(String keyword) {
        if (searchCache.containsKey(keyword)) {
            return searchCache.get(keyword);
        }

        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(HOST)
                .path("/api/v2.1/films")
                .path("/search-by-keyword")
                .queryParam("keyword", keyword)
                .build()
                .toString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(APIKEY_HEADER_NAME, apikey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        Map<String, String> params = new HashMap<>();
        params.put("keyword", keyword);

        ResponseEntity<SearchDto> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, SearchDto.class, params);

        List<FilmDto> films = Objects.requireNonNull(response.getBody()).films();
        addFilmsToCache(keyword, films);

        return films;
    }

    public FilmDto find(String filmId) {
        if (filmCache.containsKey(filmId)) {
            return filmCache.get(filmId);
        }

        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(HOST)
                .path("/api/v2.1/films")
                .path("/" + filmId)
                .build()
                .toString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(APIKEY_HEADER_NAME, apikey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<FilmData> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, FilmData.class);

        FilmDto film = Objects.requireNonNull(response.getBody()).data();
        filmCache.put(String.valueOf(film.filmId()), film);

        return film;
    }

    private void addFilmsToCache(String keyword, List<FilmDto> films) {
        searchCache.put(keyword, films);
        for (FilmDto filmDto : films) {
            filmCache.put(String.valueOf(filmDto.filmId()), filmDto);
        }
    }
}
