package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class OldPasswordMismatchException extends BaseException {
    public OldPasswordMismatchException() {
        super(HttpStatus.BAD_REQUEST, "Старый пароль не совпадает");
    }
}