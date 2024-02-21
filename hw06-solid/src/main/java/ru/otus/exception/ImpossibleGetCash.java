package ru.otus.exception;

public class ImpossibleGetCash extends RuntimeException {
    public ImpossibleGetCash(String message) {
        super(message);
    }
}
