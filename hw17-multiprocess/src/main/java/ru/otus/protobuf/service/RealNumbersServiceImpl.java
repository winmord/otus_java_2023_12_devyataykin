package ru.otus.protobuf.service;

import java.util.ArrayList;
import java.util.List;
import ru.otus.protobuf.model.Interval;
import ru.otus.protobuf.model.Number;

public class RealNumbersServiceImpl implements NumbersService {

    @Override
    public List<Number> sendInterval(Interval interval) {
        List<Number> numbers = new ArrayList<>();
        for (int i = interval.getFirstValue() + 1; i <= interval.getLastValue(); i++) {
            numbers.add(new Number(i));
        }

        return numbers;
    }
}
