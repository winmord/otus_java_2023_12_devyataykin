package ru.otus;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

class DemoTest {
    private Demo demo;

    public DemoTest() {
        // default constructor
    }

    @Before
    void firstSetup() {
        if (demo == null) {
            demo = new Demo(1, 2);
        }
    }

    @Before
    void secondSetup() {
        if (demo == null) {
            demo = new Demo();
            demo.setA(1);
            demo.setB(2);
        }
    }

    @Test
    void firstTest() {
        int actual = demo.simpleMethod(3);
        int expected = 7;

        if (actual != expected) {
            throw new ArithmeticException("1 + 2 * 3 != 7");
        }
    }

    @Test
    void secondTest() {
        int actual = demo.simpleMethod(3);
        int expected = 6;

        if (actual != expected) {
            throw new ArithmeticException("1 + 2 * 3 != 6");
        }
    }

    @After
    void firstTeardown() {
        if (demo != null) {
            demo.setA(0);
            demo.setB(0);
        }
    }

    @After
    void secondTeardown() {
        demo = null;
    }
}
