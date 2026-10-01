package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class InvalidFileTypeException extends BaseException {

    public InvalidFileTypeException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}