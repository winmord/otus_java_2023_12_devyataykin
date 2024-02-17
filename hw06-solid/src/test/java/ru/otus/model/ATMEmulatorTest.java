package ru.otus.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.exception.BanknoteSlotUpperLimitExceeded;
import ru.otus.exception.ImpossibleGetCash;
import ru.otus.exception.UnsupportedBanknoteDenomination;
import ru.otus.service.ATMService;
import ru.otus.service.ATMServiceImpl;

class ATMEmulatorTest {
    private final List<BanknoteSlot> banknoteSlots = List.of(
            new BanknoteSlot(100, 2), new BanknoteSlot(500, 3), new BanknoteSlot(1000, 4), new BanknoteSlot(5000, 5));

    private ATMEmulator atmEmulator;

    @BeforeEach
    void setUp() {
        ATMService atmService = new ATMServiceImpl(banknoteSlots);
        atmEmulator = new ATMEmulator(atmService);
    }

    @Test
    void addCashTest() {
        assertEquals(0, atmEmulator.getRemainingAmountOfCash());

        atmEmulator.addCash(List.of(new Banknote(100), new Banknote(1000)));

        int expectedSum = 100 + 1000;
        assertEquals(expectedSum, atmEmulator.getRemainingAmountOfCash());
    }

    @Test
    void addCashUpperLimitExceededTest() {
        assertEquals(0, atmEmulator.getRemainingAmountOfCash());

        List<Banknote> banknotes = List.of(new Banknote(100), new Banknote(100), new Banknote(100));

        assertThrows(BanknoteSlotUpperLimitExceeded.class, () -> atmEmulator.addCash(banknotes));
    }

    @Test
    void addCashUnsupportedSlotTest() {
        assertEquals(0, atmEmulator.getRemainingAmountOfCash());

        List<Banknote> banknotes = List.of(new Banknote(200));

        assertThrows(UnsupportedBanknoteDenomination.class, () -> atmEmulator.addCash(banknotes));
    }

    @Test
    void getCashTest() {
        assertEquals(0, atmEmulator.getRemainingAmountOfCash());

        atmEmulator.addCash(List.of(new Banknote(100), new Banknote(500), new Banknote(500), new Banknote(1000)));

        int cashSum = 1100;
        int expectedBanknotesAmount = 2;
        int expectedRemainingAmount = 500 + 500;

        assertEquals(expectedBanknotesAmount, atmEmulator.getCash(cashSum).size());
        assertEquals(expectedRemainingAmount, atmEmulator.getRemainingAmountOfCash());
    }

    @Test
    void getCashErrorTest() {
        assertEquals(0, atmEmulator.getRemainingAmountOfCash());

        atmEmulator.addCash(List.of(new Banknote(100), new Banknote(500), new Banknote(500), new Banknote(1000)));

        int cashSum = 1200;

        assertThrows(ImpossibleGetCash.class, () -> atmEmulator.getCash(cashSum));
    }
}
