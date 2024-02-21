package ru.otus.service;

import java.util.List;
import ru.otus.model.Banknote;
import ru.otus.model.BanknoteSlot;
import ru.otus.model.SlotsStorage;

public class ATMServiceImpl implements ATMService {
    SlotsStorage slotsStorage = new SlotsStorage();

    public ATMServiceImpl(List<BanknoteSlot> banknoteSlots) {
        banknoteSlots.forEach(banknoteSlot -> slotsStorage.add(banknoteSlot));
    }

    @Override
    public void addCash(List<Banknote> banknotes) {
        banknotes.forEach(banknote -> slotsStorage.addBanknote(banknote.denomination()));
    }

    @Override
    public List<Banknote> getCash(int sum) {
        return slotsStorage.getCash(sum);
    }

    @Override
    public int getRemainingAmountOfCash() {
        return slotsStorage.getCashSum();
    }
}
