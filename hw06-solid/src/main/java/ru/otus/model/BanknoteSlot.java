package ru.otus.model;

import ru.otus.exception.BanknoteSlotDoesNotContainEnough;
import ru.otus.exception.BanknoteSlotUpperLimitExceeded;

public class BanknoteSlot {
    private final int denomination;
    private final int upperLimit;
    private int count;

    public BanknoteSlot(int denomination, int upperLimit) {
        this.denomination = denomination;
        this.upperLimit = upperLimit;
    }

    public int getCount() {
        return count;
    }

    public int getDenomination() {
        return denomination;
    }

    public void addBanknote() {
        if (this.count + 1 > upperLimit) {
            throw new BanknoteSlotUpperLimitExceeded("Slot can contains " + upperLimit + "banknotes only");
        }
        this.count += 1;
    }

    public Banknote getBanknote() {
        if (this.count - 1 < 0) {
            throw new BanknoteSlotDoesNotContainEnough("Slot contains " + this.count + "banknotes only");
        }
        this.count -= 1;

        return new Banknote(denomination);
    }
}
