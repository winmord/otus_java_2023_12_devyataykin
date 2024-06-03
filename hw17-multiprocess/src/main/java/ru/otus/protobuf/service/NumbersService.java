package ru.otus.protobuf.service;

import java.util.List;
import ru.otus.protobuf.model.Interval;
import ru.otus.protobuf.model.Number;

public interface NumbersService {
    List<Number> sendInterval(Interval interval);
}
