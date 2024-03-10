package ru.otus.processor.homework;

public class EvenSecondException extends RuntimeException {
    public EvenSecondException(Throwable cause) {
        super(cause);
    }

    public EvenSecondException(String message) {
        super(message);
    }
}
