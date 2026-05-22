package ru.pulsecore.app.modules.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String type;

    public BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.type = null;
    }

    public BaseException(HttpStatus status, String message, String type) {
        super(message);
        this.status = status;
        this.type = type;
    }
}