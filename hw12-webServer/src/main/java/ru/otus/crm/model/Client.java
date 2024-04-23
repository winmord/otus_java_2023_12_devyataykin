package ru.otus.crm.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public final class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    @Expose
    private Long id;

    @Column(name = "name")
    @Expose
    private String name;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "client")
    private Address address;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "client")
    private List<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = new Address(address.getId(), address.getStreet(), this);
        this.phones = phones.stream()
                .map(phone -> new Phone(phone.getId(), phone.getNumber(), this))
                .toList();
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        return new Client(
                this.id,
                this.name,
                this.address.copy(),
                this.phones.stream().map(Phone::copy).toList());
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
