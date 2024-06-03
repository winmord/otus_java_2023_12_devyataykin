package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Homework {
    private static final Logger logger = LoggerFactory.getLogger(Homework.class);
    private String last = "second";

    private synchronized void action(String turn, Counter counter) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (last.equals(turn)) {
                    this.wait();
                }

                logger.info("{}", counter.getValue());
                last = turn;
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static class Counter {
        private int value;
        private int valueAdd = 1;

        public int getValue() {
            value += valueAdd;
            if (value > 9) {
                valueAdd *= -1;
            }

            return value;
        }
    }

    public static void main(String[] args) {
        Homework homework = new Homework();
        new Thread(() -> homework.action("first", new Counter())).start();
        sleep();
        new Thread(() -> homework.action("second", new Counter())).start();
    }
}
