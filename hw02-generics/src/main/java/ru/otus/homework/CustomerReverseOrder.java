package ru.otus.homework;

import java.util.*;

public class CustomerReverseOrder {
    private final Deque<Customer> customers = new ArrayDeque<>();

    public void add(Customer customer) {
        customers.push(customer);
    }

    public Customer take() {
        return customers.pop();
    }
}
