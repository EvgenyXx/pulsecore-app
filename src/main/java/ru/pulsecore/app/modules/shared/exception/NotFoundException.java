package ru.pulsecore.app.modules.shared.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(Long messageId) {
        super(HttpStatus.NOT_FOUND, "Сообщение не найдено :" + messageId );
    }
}