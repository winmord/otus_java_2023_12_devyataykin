package ru.otus.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ru.otus.exception.BanknoteSlotUpperLimitExceeded;
import ru.otus.exception.BanknoteSlotsAreFull;
import ru.otus.exception.UnsupportedBanknoteDenomination;
import ru.otus.model.Banknote;
import ru.otus.model.BanknoteSlot;

public class ATMServiceImpl implements ATMService {
    private final Map<Integer, LinkedList<BanknoteSlot>> banknoteSlots;

    public ATMServiceImpl(Map<Integer, LinkedList<BanknoteSlot>> banknoteSlots) {
        this.banknoteSlots = banknoteSlots;
    }

    @Override
    public void addCash(List<Banknote> banknotes) {
        for (Banknote banknote : banknotes) {
            int denomination = banknote.getDenomination();
            LinkedList<BanknoteSlot> slots = banknoteSlots.get(denomination);

            if (slots.isEmpty()) {
                throw new UnsupportedBanknoteDenomination(
                        "There is no slot for " + banknote.getDenomination() + "banknotes");
            }

            for (BanknoteSlot slot : slots) {
                try {
                    slot.addBanknotes(1);
                    break;
                } catch (BanknoteSlotUpperLimitExceeded exc) {
                    if (slot.equals(slots.getLast())) {
                        throw new BanknoteSlotsAreFull("Banknote slots for " + denomination + " are full");
                    }
                }
            }
        }
    }

    @Override
    public List<Banknote> getCash(int sum) {
        return List.of();
    }
}
