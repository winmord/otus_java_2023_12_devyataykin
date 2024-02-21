package ru.otus.model;

import java.util.*;
import ru.otus.exception.ImpossibleGetCash;
import ru.otus.exception.UnsupportedBanknoteDenomination;

public class SlotsStorage {
    private final Map<Denomination, BanknoteSlot> banknoteSlots = new TreeMap<>(Comparator.reverseOrder());

    public void add(BanknoteSlot banknoteSlot) {
        this.banknoteSlots.put(banknoteSlot.getDenomination(), banknoteSlot);
    }

    public void addBanknote(Denomination denomination) {
        if (!banknoteSlots.containsKey(denomination)) {
            throw new UnsupportedBanknoteDenomination("There is no slot for " + denomination + "banknotes");
        }

        BanknoteSlot slot = banknoteSlots.get(denomination);
        slot.addBanknote();
    }

    public List<Banknote> getCash(int sum) {
        List<Banknote> cash = new ArrayList<>();
        EnumMap<Denomination, Integer> banknotes = new EnumMap<>(Denomination.class);

        for (var entry : banknoteSlots.entrySet()) {
            if (sum != 0) {
                Denomination denomination = entry.getKey();
                while (sum >= Denomination.getValue(denomination)
                        && areNeededBanknotesFewerThanExists(banknotes, denomination)) {
                    banknotes.put(denomination, banknotes.getOrDefault(denomination, 0) + 1);
                    sum -= Denomination.getValue(denomination);
                }
            }
        }

        if (sum != 0) {
            throw new ImpossibleGetCash("There are no banknotes for getting cash");
        }

        for (var entry : banknotes.entrySet()) {
            BanknoteSlot slot = banknoteSlots.get(entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
                cash.add(slot.getBanknote());
            }
        }

        return cash;
    }

    public int getCashSum() {
        int amount = 0;

        for (var entry : banknoteSlots.entrySet()) {
            amount += Denomination.getValue(entry.getKey()) * entry.getValue().getCount();
        }

        return amount;
    }

    private boolean areNeededBanknotesFewerThanExists(Map<Denomination, Integer> banknotes, Denomination denomination) {
        return banknotes.getOrDefault(denomination, 0) + 1
                <= banknoteSlots.get(denomination).getCount();
    }
}
