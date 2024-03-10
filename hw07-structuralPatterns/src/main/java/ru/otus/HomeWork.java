package ru.otus;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.homework.ProcessorSwapFields11And12;
import ru.otus.processor.homework.ProcessorThrowOnEvenSecond;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var listenerPrinter = new ListenerPrinterConsole();

        var swapProcessor = new ProcessorSwapFields11And12();
        var processorThrowOnEvenSecond = new ProcessorThrowOnEvenSecond(LocalDateTime::now);

        var processors = List.of(swapProcessor, processorThrowOnEvenSecond);
        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        complexProcessor.addListener(listenerPrinter);

        List<String> field13Data = List.of("field13 data");
        ObjectForMessage field13 = new ObjectForMessage();
        field13.setData(field13Data);

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11")
                .field12("field12")
                .field13(field13)
                .build();

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        complexProcessor.removeListener(listenerPrinter);
    }
}
