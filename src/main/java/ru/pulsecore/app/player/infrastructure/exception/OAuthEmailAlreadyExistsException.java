package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class OAuthEmailAlreadyExistsException extends BaseException {
    public OAuthEmailAlreadyExistsException(String provider) {
        super(HttpStatus.CONFLICT,
                "Этот email уже используется через " + provider +
                        ". Войдите через " + provider + ".",
                "OAUTH_EMAIL");
    }
}