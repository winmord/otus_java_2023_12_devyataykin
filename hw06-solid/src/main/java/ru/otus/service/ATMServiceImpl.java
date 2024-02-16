package ru.otus.service;

import java.util.*;
import ru.otus.exception.ImpossibleGetCash;
import ru.otus.exception.UnsupportedBanknoteDenomination;
import ru.otus.model.Banknote;
import ru.otus.model.BanknoteSlot;

public class ATMServiceImpl implements ATMService {
    private final Map<Integer, BanknoteSlot> banknoteSlots = new TreeMap<>(Comparator.reverseOrder());

    public ATMServiceImpl(List<BanknoteSlot> banknoteSlots) {
        for (BanknoteSlot banknoteSlot : banknoteSlots) {
            this.banknoteSlots.put(banknoteSlot.getDenomination(), banknoteSlot);
        }
    }

    @Override
    public void addCash(List<Banknote> banknotes) {
        for (Banknote banknote : banknotes) {
            int denomination = banknote.getDenomination();

            if (!banknoteSlots.containsKey(denomination)) {
                throw new UnsupportedBanknoteDenomination("There is no slot for " + denomination + "banknotes");
            }

            BanknoteSlot slot = banknoteSlots.get(denomination);
            slot.addBanknote();
        }
    }

    @Override
    public List<Banknote> getCash(int sum) {
        List<Banknote> cash = new ArrayList<>();
        Map<Integer, Integer> banknotes = new HashMap<>();

        for (var entry : banknoteSlots.entrySet()) {
            int denomination = entry.getKey();
            while (sum >= denomination) {
                int banknotesAmount = banknotes.getOrDefault(denomination, 0) + 1;

                if (banknotesAmount > banknoteSlots.get(denomination).getCount()) break;

                banknotes.put(denomination, banknotes.getOrDefault(denomination, 0) + 1);
                sum -= denomination;
            }
            if (sum == 0) break;
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

    @Override
    public int getRemainingAmountOfCash() {
        int amount = 0;

        for (var entry : banknoteSlots.entrySet()) {
            amount += entry.getKey() * entry.getValue().getCount();
        }

        return amount;
    }
}
