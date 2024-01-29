package ru.otus;

public class TestResults {
    private int passedTests;
    private int failedTests;

    public int getPassedTests() {
        return passedTests;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public void countPassed() {
        ++passedTests;
    }

    public void countFailed() {
        ++failedTests;
    }
}
