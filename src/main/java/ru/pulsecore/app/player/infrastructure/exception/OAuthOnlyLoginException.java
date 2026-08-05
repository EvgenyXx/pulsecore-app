package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class OAuthOnlyLoginException extends BaseException {
    public OAuthOnlyLoginException(String provider) {
        super(HttpStatus.FORBIDDEN,
                "Вы зарегистрированы через " + provider +
                        ". Войдите через " + provider + " или установите пароль в профиле.",
                "OAUTH_ONLY");
    }
}