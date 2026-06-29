package ru.pulsecore.app.modules.shared.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Можно редактировать только свои сообщения");
    }
}