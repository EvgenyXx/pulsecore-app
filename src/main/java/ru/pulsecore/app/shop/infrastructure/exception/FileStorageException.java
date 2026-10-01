package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class FileStorageException extends BaseException {

    public FileStorageException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}