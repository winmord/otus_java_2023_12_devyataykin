package ru.otus.model;

public record Banknote(Denomination denomination) {

    @Override
    public String toString() {
        return String.valueOf(denomination);
    }
}
