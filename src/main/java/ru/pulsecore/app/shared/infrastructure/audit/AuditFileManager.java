package ru.pulsecore.app.shared.infrastructure.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.exception.AuditFileCreationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditFileManager {

    private final AuditProperties properties;

    public Path resolveDailyFile(String prefix) {
        Path folder = Path.of(properties.getDir()).resolve(prefix);
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new AuditFileCreationException(folder.toString());
        }
        return folder.resolve(prefix + "-" + LocalDate.now() + ".log");
    }


}