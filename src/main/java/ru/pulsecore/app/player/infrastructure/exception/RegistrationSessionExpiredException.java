package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class RegistrationSessionExpiredException extends BaseException {
    public RegistrationSessionExpiredException() {
        super(HttpStatus.BAD_REQUEST, "Сессия регистрации истекла. Начните заново.");
    }
}