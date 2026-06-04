package ru.pulsecore.app.modules.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.exception.SqlReadException;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SqlReader {

    private final ResourceLoader resourceLoader;

    public String read(String path) {
        try {
            return resourceLoader.getResource("classpath:" + path)
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SqlReadException(path, e);
        }
    }
}