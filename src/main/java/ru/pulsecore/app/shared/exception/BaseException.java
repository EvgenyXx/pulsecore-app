package ru.pulsecore.app.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String type;
    private final String telegramUrl;

    public BaseException(HttpStatus status, String message) {
        this(status, message, null, null);
    }

    public BaseException(HttpStatus status, String message, String type) {
        this(status, message, type, null);
    }

    public BaseException(HttpStatus status, String message, String type, String telegramUrl) {
        super(message);
        this.status = status;
        this.type = type;
        this.telegramUrl = telegramUrl;
    }
}