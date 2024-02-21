package ru.otus.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.exception.BanknoteSlotUpperLimitExceeded;
import ru.otus.exception.ImpossibleGetCash;
import ru.otus.service.ATMService;
import ru.otus.service.ATMServiceImpl;

class ATMServiceTest {
    private final List<BanknoteSlot> banknoteSlots = List.of(
            new BanknoteSlot(Denomination.ONE_HUNDRED, 2),
            new BanknoteSlot(Denomination.FIVE_HUNDRED, 3),
            new BanknoteSlot(Denomination.ONE_THOUSAND, 4),
            new BanknoteSlot(Denomination.FIVE_THOUSAND, 5));

    private ATMService atmService;

    @BeforeEach
    void setUp() {
        atmService = new ATMServiceImpl(banknoteSlots);
    }

    @Test
    void addCashTest() {
        assertEquals(0, atmService.getRemainingAmountOfCash());

        atmService.addCash(List.of(new Banknote(Denomination.ONE_HUNDRED), new Banknote(Denomination.ONE_THOUSAND)));

        int expectedSum = 100 + 1000;
        assertEquals(expectedSum, atmService.getRemainingAmountOfCash());
    }

    @Test
    void addCashUpperLimitExceededTest() {
        assertEquals(0, atmService.getRemainingAmountOfCash());

        List<Banknote> banknotes = List.of(
                new Banknote(Denomination.ONE_HUNDRED),
                new Banknote(Denomination.ONE_HUNDRED),
                new Banknote(Denomination.ONE_HUNDRED));

        assertThrows(BanknoteSlotUpperLimitExceeded.class, () -> atmService.addCash(banknotes));
    }

    @Test
    void getCashTest() {
        assertEquals(0, atmService.getRemainingAmountOfCash());

        atmService.addCash(List.of(
                new Banknote(Denomination.ONE_HUNDRED),
                new Banknote(Denomination.FIVE_HUNDRED),
                new Banknote(Denomination.FIVE_HUNDRED),
                new Banknote(Denomination.ONE_THOUSAND)));

        int cashSum = 1100;
        int expectedBanknotesAmount = 2;
        int expectedRemainingAmount = 500 + 500;

        assertEquals(expectedBanknotesAmount, atmService.getCash(cashSum).size());
        assertEquals(expectedRemainingAmount, atmService.getRemainingAmountOfCash());
    }

    @Test
    void getCashErrorTest() {
        assertEquals(0, atmService.getRemainingAmountOfCash());

        atmService.addCash(List.of(
                new Banknote(Denomination.ONE_HUNDRED),
                new Banknote(Denomination.FIVE_HUNDRED),
                new Banknote(Denomination.FIVE_HUNDRED),
                new Banknote(Denomination.FIVE_THOUSAND)));

        int cashSum = 1200;

        assertThrows(ImpossibleGetCash.class, () -> atmService.getCash(cashSum));
    }
}
