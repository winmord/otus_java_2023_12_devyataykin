package ru.otus.service;

import java.util.List;
import ru.otus.model.Banknote;

public interface ATMService {
    void addCash(List<Banknote> banknotes);

    List<Banknote> getCash(int sum);
}
