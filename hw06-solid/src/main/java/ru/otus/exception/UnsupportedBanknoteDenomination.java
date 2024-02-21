package ru.otus.exception;

public class UnsupportedBanknoteDenomination extends RuntimeException {
    public UnsupportedBanknoteDenomination(String message) {
        super(message);
    }
}
