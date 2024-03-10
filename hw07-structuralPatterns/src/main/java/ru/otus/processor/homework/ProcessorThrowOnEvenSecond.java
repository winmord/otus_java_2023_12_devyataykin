package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorThrowOnEvenSecond implements Processor {
    private final DateTimeProvider dateTimeProvider;

    public ProcessorThrowOnEvenSecond(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        long currentSecond = dateTimeProvider.getDate().getSecond();
        if (currentSecond % 2 == 0) {
            throw new EvenSecondException("Current second is even: " + currentSecond);
        }

        return message;
    }
}
