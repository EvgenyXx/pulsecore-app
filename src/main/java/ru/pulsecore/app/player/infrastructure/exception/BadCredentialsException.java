package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class BadCredentialsException extends BaseException {
    public BadCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
    }
}