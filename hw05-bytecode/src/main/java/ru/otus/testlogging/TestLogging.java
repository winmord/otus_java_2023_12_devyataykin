package ru.otus.testlogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.Log;

public class TestLogging implements TestLoggingInterface {
    private static final Logger logger = LoggerFactory.getLogger(TestLogging.class);

    @Log
    @Override
    public void calculation() {
        logger.info("calculate without parameters");
    }

    @Log
    @Override
    public void calculation(int param) {
        logger.info("calculate with one parameter");
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        logger.info("calculate with two common type parameters");
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        logger.info("calculate with two common type and one another parameters");
    }
}
