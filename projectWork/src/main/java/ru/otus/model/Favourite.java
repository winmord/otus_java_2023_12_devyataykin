package ru.otus.model;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@Table(name = "favourite")
public class Favourite {
    @Id
    private Long id;

    @Nonnull
    private Long chatId;

    @Nonnull
    @NotEmpty
    @NotBlank
    private String filmName;

    @Nonnull
    @NotEmpty
    @NotBlank
    private String filmYear;

    @Nonnull
    @NotEmpty
    @NotBlank
    private String filmId;

    @PersistenceCreator
    public Favourite(Long id, @Nonnull Long chatId, @Nonnull String filmName, @Nonnull String filmYear, @Nonnull String filmId) {
        this.id = id;
        this.chatId = chatId;
        this.filmName = filmName;
        this.filmYear = filmYear;
        this.filmId = filmId;
    }

    @Override
    public String toString() {
        return "Favourite{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", filmName='" + filmName + '\'' +
                ", filmYear=" + filmYear +
                ", filmId='" + filmId + '\'' +
                '}';
    }
}
