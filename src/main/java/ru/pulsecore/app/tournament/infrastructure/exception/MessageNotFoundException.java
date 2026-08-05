package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class MessageNotFoundException extends BaseException {
    public MessageNotFoundException(Long messageId) {
        super(HttpStatus.NOT_FOUND, "Сообщение не найдено :" + messageId );
    }
}