package ru.otus;

import ru.otus.proxy.Ioc;
import ru.otus.testlogging.TestLogging;
import ru.otus.testlogging.TestLoggingInterface;

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface testLoggingInterface = (TestLoggingInterface) Ioc.createClass(new TestLogging());
        testLoggingInterface.calculation();
        testLoggingInterface.calculation(1);
        testLoggingInterface.calculation(1, 2);
        testLoggingInterface.calculation(1, 2, "third_parameter");
    }
}
