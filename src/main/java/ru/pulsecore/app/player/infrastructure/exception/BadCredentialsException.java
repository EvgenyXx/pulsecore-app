package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class BadCredentialsException extends BaseException {

    public BadCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
    }


    protected BadCredentialsException(HttpStatus status, String message) {
        super(status, message);
    }

    public static class SessionExpired extends BadCredentialsException {
        public SessionExpired() {
            super(HttpStatus.UNAUTHORIZED, "Сессия истекла");
        }
    }


    public static class InvalidCode extends BadCredentialsException {
        public InvalidCode() {
            super(HttpStatus.UNAUTHORIZED, "Неверный код подтверждения");
        }
    }
}