package ru.otus.dataprocessor;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(FileSerializer.class);
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        String jsonData = new Gson().toJson(data);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(fileName))) {
            bufferedWriter.write(jsonData);
        } catch (IOException exc) {
            logger.error("Failed to process {}", fileName);
        }
    }
}
