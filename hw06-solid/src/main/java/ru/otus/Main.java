package ru.otus;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.model.ATMEmulator;
import ru.otus.model.Banknote;
import ru.otus.model.BanknoteSlot;
import ru.otus.service.ATMService;
import ru.otus.service.ATMServiceImpl;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        List<BanknoteSlot> banknoteSlots = List.of(
                new BanknoteSlot(100, 10),
                new BanknoteSlot(500, 10),
                new BanknoteSlot(1000, 10),
                new BanknoteSlot(5000, 10));

        ATMService atmService = new ATMServiceImpl(banknoteSlots);
        ATMEmulator atmEmulator = new ATMEmulator(atmService);

        List<Banknote> banknotes = List.of(
                new Banknote(100),
                new Banknote(100),
                new Banknote(100),
                new Banknote(1000),
                new Banknote(1000),
                new Banknote(1000),
                new Banknote(1000),
                new Banknote(1000),
                new Banknote(5000));

        logger.info("{}", atmEmulator.getRemainingAmountOfCash());
        atmEmulator.addCash(banknotes);
        logger.info("{}", atmEmulator.getRemainingAmountOfCash());
        logger.info("{}", atmEmulator.getCash(4200));
        logger.info("{}", atmEmulator.getRemainingAmountOfCash());

        try {
            logger.info("{}", atmEmulator.getCash(200));
        } catch (RuntimeException exc) {
            logger.info(exc.getMessage());
        }

        try {
            logger.info("{}", atmEmulator.getCash(7000));
        } catch (RuntimeException exc) {
            logger.info(exc.getMessage());
        }
    }
}
