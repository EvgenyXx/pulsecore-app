package ru.pulsecore.app.modules.player_modeles.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class PlayerNotFoundException extends BaseException {
    public PlayerNotFoundException(String playerId) {
        super(HttpStatus.NOT_FOUND, "Игрок не найден: " + playerId);
    }
}