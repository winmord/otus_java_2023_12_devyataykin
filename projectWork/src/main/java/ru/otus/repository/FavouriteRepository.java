package ru.otus.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import ru.otus.model.Favourite;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends ListCrudRepository<Favourite, Long> {
    Optional<Favourite> findByChatIdAndFilmId(Long chatId, String filmId);

    Collection<Favourite> findAllByChatId(Long chatId);
}
