package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class OAuthEmailAlreadyExistsException extends BaseException {
    public OAuthEmailAlreadyExistsException(String provider) {
        super(HttpStatus.CONFLICT,
                "Этот email уже используется через " + provider +
                        ". Войдите через " + provider + ".",
                "OAUTH_EMAIL");
    }
}