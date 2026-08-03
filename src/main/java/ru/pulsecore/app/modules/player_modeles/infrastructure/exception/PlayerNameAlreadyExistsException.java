package ru.pulsecore.app.modules.player_modeles.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class PlayerNameAlreadyExistsException extends BaseException {
    public PlayerNameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Игрок с таким именем уже существует");
    }
}