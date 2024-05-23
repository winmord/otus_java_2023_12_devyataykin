package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Homework {
    private static final Logger logger = LoggerFactory.getLogger(Homework.class);
    private int first = 1;
    private int firstAdd = 1;
    private int second = 1;
    private int secondAdd = 1;
    private String last = "second";

    private synchronized void action(String turn) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (last.equals(turn)) {
                    this.wait();
                }

                if ("first".equals(turn)) {
                    logger.info("{}", first);
                    first += firstAdd;
                    if (first > 9) {
                        firstAdd *= -1;
                    }
                } else {
                    logger.info("{}", second);
                    second += secondAdd;
                    if (second > 9) {
                        secondAdd *= -1;
                    }
                }

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

    public static void main(String[] args) {
        Homework homework = new Homework();
        new Thread(() -> homework.action("first")).start();
        sleep();
        new Thread(() -> homework.action("second")).start();
    }
}
