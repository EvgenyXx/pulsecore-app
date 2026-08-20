package ru.pulsecore.app.shared.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Можно редактировать только свои сообщения");
    }
}