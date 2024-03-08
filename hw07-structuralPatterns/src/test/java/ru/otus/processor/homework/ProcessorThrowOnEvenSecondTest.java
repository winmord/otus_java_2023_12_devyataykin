package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import ru.otus.handler.ComplexProcessor;
import ru.otus.model.Message;
import ru.otus.processor.Processor;

class ProcessorThrowOnEvenSecondTest {

    @Test
    void processEvenSecond() {
        var processorThrowOnEvenSecond = new ProcessorThrowOnEvenSecond(() -> {
            LocalDateTime now = LocalDateTime.now();
            if (now.getSecond() % 2 != 0) {
                now = now.plusSeconds(1);
            }
            return now;
        });

        Consumer<Exception> errorHandler = e -> {
            throw new EvenSecondException(e);
        };

        List<Processor> processors = List.of(processorThrowOnEvenSecond);
        var complexProcessor = new ComplexProcessor(processors, errorHandler);

        var message = new Message.Builder(1L).build();

        assertThrows(EvenSecondException.class, () -> complexProcessor.handle(message));
    }

    @Test
    void processOddSecond() {
        var processorThrowOnEvenSecond = new ProcessorThrowOnEvenSecond(() -> {
            LocalDateTime now = LocalDateTime.now();
            if (now.getSecond() % 2 == 0) {
                now = now.plusSeconds(1);
            }
            return now;
        });

        Consumer<Exception> errorHandler = e -> {
            throw new RuntimeException(e);
        };

        List<Processor> processors = List.of(processorThrowOnEvenSecond);
        var complexProcessor = new ComplexProcessor(processors, errorHandler);
        var message = new Message.Builder(1L).build();

        assertDoesNotThrow(() -> complexProcessor.handle(message));
    }
}
