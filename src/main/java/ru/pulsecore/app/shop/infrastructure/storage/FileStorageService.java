package ru.pulsecore.app.shop.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pulsecore.app.shop.infrastructure.exception.FileStorageException;
import ru.pulsecore.app.shop.infrastructure.exception.InvalidFileTypeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageProperties props;

    public void deleteAll(List<String> urls) {
        if (urls == null || urls.isEmpty()) return;

        Path base = Path.of(props.getUploadDir()).toAbsolutePath().normalize();

        for (String url : urls) {
            if (url == null || !url.startsWith("/uploads/")) continue;

            String relative = url.substring("/uploads/".length());
            Path target = base.resolve(relative).normalize();

            if (!target.startsWith(base)) continue;

            try {
                Files.deleteIfExists(target);
            } catch (IOException e) {
                log.warn("Не удалось удалить файл: {}", url, e);
            }
        }
    }

    public String save(MultipartFile file, String subdir) {
        validate(file);

        String ext = extractExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + ext;

        Path dir = Path.of(props.getUploadDir()).resolve(subdir);
        Path target = dir.resolve(fileName);

        try {
            Files.createDirectories(dir);
            file.transferTo(target.toAbsolutePath().toFile());
        } catch (IOException e) {
            throw new FileStorageException("Не удалось сохранить файл: " + fileName);
        }

        return "/uploads/" + subdir + "/" + fileName;
    }

    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) return;

        String relative = url.substring("/uploads/".length());

        Path base = Path.of(props.getUploadDir()).toAbsolutePath().normalize();
        Path target = base.resolve(relative).normalize();

        if (!target.startsWith(base)) return;

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось удалить файл: " + url);
        }
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("Файл пустой");
        }
        if (file.getSize() > props.getMaxFileSize()) {
            throw new InvalidFileTypeException("Файл слишком большой");
        }
        if (!props.getAllowedTypes().contains(file.getContentType())) {
            throw new InvalidFileTypeException("Неверный тип файла: " + file.getContentType());
        }
    }

    private String extractExtension(String name) {
        if (name == null) return ".jpg";
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i).toLowerCase() : ".jpg";
    }
}