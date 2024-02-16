package ru.otus.model;

import java.util.List;
import ru.otus.service.ATMService;

public class ATMEmulator {
    private final ATMService atmService;

    public ATMEmulator(ATMService atmService) {
        this.atmService = atmService;
    }

    public void addCash(List<Banknote> banknotes) {
        atmService.addCash(banknotes);
    }

    public List<Banknote> getCash(int sum) {
        return atmService.getCash(sum);
    }

    public int getRemainingAmountOfCash() {
        return atmService.getRemainingAmountOfCash();
    }
}
