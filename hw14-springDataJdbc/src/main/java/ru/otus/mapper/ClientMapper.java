package ru.otus.mapper;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import ru.otus.dto.ClientDto;
import ru.otus.exceptions.UtilityClassException;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

public class ClientMapper {
    private ClientMapper() {
        throw new UtilityClassException("Utility class");
    }

    public static Client toClient(ClientDto clientDto) {
        Address address = Address.builder().street(clientDto.getStreet()).build();

        Set<Phone> phones = Arrays.stream(clientDto.getPhones().split(","))
                .map(number -> new Phone(number, null))
                .collect(Collectors.toSet());

        return Client.builder()
                .name(clientDto.getName())
                .address(address)
                .phones(phones)
                .build();
    }
}
