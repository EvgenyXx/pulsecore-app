package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class OldPasswordMismatchException extends BaseException {
    public OldPasswordMismatchException() {
        super(HttpStatus.BAD_REQUEST, "Старый пароль не совпадает");
    }
}