package ru.otus.exception;

public class BanknoteSlotDoesNotContainEnough extends RuntimeException {
    public BanknoteSlotDoesNotContainEnough(String message) {
        super(message);
    }
}
