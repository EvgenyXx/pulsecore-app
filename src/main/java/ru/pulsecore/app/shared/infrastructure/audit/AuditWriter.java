package ru.pulsecore.app.shared.infrastructure.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditWriter {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AuditFileManager fileManager;

    public void write(String prefix, String message) {
        Path file = fileManager.resolveDailyFile(prefix);
        String line = LocalDateTime.now().format(TS) + " | " + message + System.lineSeparator();
        try {
            Files.writeString(file, line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.warn("Аудит: не удалось записать в файл {}", file, e);
        }
    }
}