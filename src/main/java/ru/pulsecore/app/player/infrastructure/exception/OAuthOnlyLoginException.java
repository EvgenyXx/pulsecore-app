package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class OAuthOnlyLoginException extends BaseException {
    public OAuthOnlyLoginException(String provider) {
        super(
                HttpStatus.FORBIDDEN,
                String.format("Этот аккаунт создан через %s. Используйте вход через %s.", provider, provider)
        );
    }
}