package ru.pulsecore.app.modules.player_modeles.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class OAuthEmailNotReceivedException extends BaseException {
    public OAuthEmailNotReceivedException() {
        super(HttpStatus.BAD_REQUEST, "Не удалось получить email от OAuth-провайдера. Попробуйте позже.");
    }
}