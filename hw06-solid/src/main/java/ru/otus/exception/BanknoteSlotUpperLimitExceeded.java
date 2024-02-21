package ru.otus.exception;

public class BanknoteSlotUpperLimitExceeded extends RuntimeException {
    public BanknoteSlotUpperLimitExceeded(String message) {
        super(message);
    }
}
