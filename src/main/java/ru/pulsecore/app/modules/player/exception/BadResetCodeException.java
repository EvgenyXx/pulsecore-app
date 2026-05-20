package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class BadResetCodeException extends BaseException {
    public BadResetCodeException() {
        super(HttpStatus.BAD_REQUEST, "Неверный код сброса");
    }
}