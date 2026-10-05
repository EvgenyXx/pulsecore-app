package ru.pulsecore.app.shop.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.pulsecore.app.shop.infrastructure.exception.InvalidFileTypeException;

@Component
@RequiredArgsConstructor
public class FileValidator {

    private final FileStorageProperties props;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("Файл пустой");
        }

        if (file.getSize() > props.getMaxFileSize()) {
            throw new InvalidFileTypeException("Файл слишком большой");
        }

        String contentType = file.getContentType();
        if (contentType == null || !props.getAllowedTypes().contains(contentType)) {
            throw new InvalidFileTypeException("Неверный тип файла: " + contentType);
        }
    }
}