package ru.otus.exception;

public class BanknoteSlotsAreFull extends RuntimeException {
    public BanknoteSlotsAreFull(String message) {
        super(message);
    }
}
