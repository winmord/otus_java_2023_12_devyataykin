package ru.otus.dataprocessor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private static final Logger logger = LoggerFactory.getLogger(ResourcesFileLoader.class);
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("Failed to process file " + fileName);
            }
            String stringData = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            return new Gson().fromJson(stringData, new TypeToken<List<Measurement>>() {}.getType());
        } catch (IOException exc) {
            logger.error(exc.getMessage());
        }

        return Collections.emptyList();
    }
}
