package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {
    private final Map<Long, Message> storage = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        long id = msg.getId();
        Message updatedMsg = msg.getSavedState();
        storage.put(id, updatedMsg);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.of(storage.get(id));
    }
}
