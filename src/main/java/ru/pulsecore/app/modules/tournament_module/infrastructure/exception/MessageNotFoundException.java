package ru.pulsecore.app.modules.tournament_module.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class MessageNotFoundException extends BaseException {
    public MessageNotFoundException(Long messageId) {
        super(HttpStatus.NOT_FOUND, "Сообщение не найдено :" + messageId );
    }
}