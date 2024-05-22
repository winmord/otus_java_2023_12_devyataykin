package ru.otus.model;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@Table(name = "address")
public class Address {

    @Id
    private final Long id;

    @Nonnull
    private final String street;

    @Nonnull
    private final Long clientId;

    public Address(String street, Long clientId) {
        this(null, street, clientId);
    }

    @PersistenceCreator
    public Address(Long id, String street, Long clientId) {
        this.id = id;
        this.street = street;
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "Address{" + "id=" + id + ", street='" + street + '\'' + ", clientId=" + clientId + '}';
    }
}
