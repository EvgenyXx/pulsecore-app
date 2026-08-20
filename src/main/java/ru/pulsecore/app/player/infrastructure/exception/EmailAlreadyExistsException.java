package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Email уже используется");
    }
}