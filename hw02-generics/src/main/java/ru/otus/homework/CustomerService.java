package ru.otus.homework;

import java.util.*;

public class CustomerService {
    private final Map<Customer, String> customerData = new TreeMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> result = ((TreeMap<Customer, String>) customerData).firstEntry();

        Customer smallestScoreCustomer = new Customer(
                result.getKey().getId(),
                result.getKey().getName(),
                result.getKey().getScores());

        return new AbstractMap.SimpleEntry<>(smallestScoreCustomer, result.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        for (Map.Entry<Customer, String> entry : customerData.entrySet()) {
            if (entry.getKey().getScores() > customer.getScores()) {
                Customer nextCustomer = new Customer(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getKey().getScores());

                return new AbstractMap.SimpleEntry<>(nextCustomer, entry.getValue());
            }
        }

        return null;
    }

    public void add(Customer customer, String data) {
        customerData.put(customer, data);
    }
}
