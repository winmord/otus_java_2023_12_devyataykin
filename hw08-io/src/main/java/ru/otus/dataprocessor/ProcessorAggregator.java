package ru.otus.dataprocessor;

import java.util.*;
import ru.otus.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        Map<String, Double> result = new TreeMap<>();

        for (Measurement measurement : data) {
            String name = measurement.name();
            Double value = measurement.value();
            result.put(name, result.getOrDefault(name, 0.) + value);
        }

        return result;
    }
}
