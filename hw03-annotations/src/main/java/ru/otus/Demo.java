package ru.otus;

public class Demo {
    private int a;
    private int b;

    public Demo() {}

    public Demo(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    int simpleMethod(int c) {
        return a + b * c;
    }
}
