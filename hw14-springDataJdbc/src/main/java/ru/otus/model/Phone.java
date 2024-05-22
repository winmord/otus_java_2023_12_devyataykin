package ru.otus.model;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@Table(name = "phone")
public class Phone {

    @Id
    private Long id;

    @Nonnull
    private final String number;

    private final Long clientId;

    public Phone(String number, Long clientId) {
        this(null, number, clientId);
    }

    @PersistenceCreator
    public Phone(Long id, String number, Long clientId) {
        this.id = id;
        this.number = number;
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return number;
    }
}
