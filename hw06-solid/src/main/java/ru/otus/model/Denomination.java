package ru.otus.model;

public enum Denomination {
    ONE_HUNDRED,
    FIVE_HUNDRED,
    ONE_THOUSAND,
    FIVE_THOUSAND;

    public static int getValue(Denomination denomination) {
        return switch (denomination) {
            case ONE_HUNDRED -> 100;
            case FIVE_HUNDRED -> 500;
            case ONE_THOUSAND -> 1000;
            case FIVE_THOUSAND -> 5000;
        };
    }
}
