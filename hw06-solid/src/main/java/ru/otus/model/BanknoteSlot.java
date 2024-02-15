package ru.otus.model;

import ru.otus.exception.BanknoteSlotDoesNotContainEnough;
import ru.otus.exception.BanknoteSlotUpperLimitExceeded;

public class BanknoteSlot {
    private final Banknote banknote;
    private final int upperLimit;
    private int count;

    public BanknoteSlot(Banknote banknote, int count, int upperLimit) {
        this.banknote = banknote;
        this.count = count;
        this.upperLimit = upperLimit;
    }

    public int getDenomination() {
        return banknote.getDenomination();
    }

    public void addBanknotes(int count) {
        if (this.count + count > upperLimit) {
            throw new BanknoteSlotUpperLimitExceeded("Slot can contains " + upperLimit + "banknotes only");
        }
        this.count += count;
    }

    public void getBanknotes(int count) {
        if (this.count - count < 0) {
            throw new BanknoteSlotDoesNotContainEnough("Slot contains " + this.count + "banknotes only");
        }
        this.count -= count;
    }
}
