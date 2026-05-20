package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class BadCredentialsException extends BaseException {
    public BadCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
    }
}