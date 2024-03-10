package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.otus.handler.ComplexProcessor;
import ru.otus.model.Message;
import ru.otus.processor.Processor;

class ProcessorSwapFields11And12Test {

    @Test
    void processSwap() {
        var swapProcessor = new ProcessorSwapFields11And12();
        List<Processor> processors = List.of(swapProcessor);
        var complexProcessor = new ComplexProcessor(processors, ex -> {});

        var message =
                new Message.Builder(1L).field11("field11").field12("field12").build();

        message = complexProcessor.handle(message);
        assertEquals("field12", message.getField11());
        assertEquals("field11", message.getField12());
    }
}
