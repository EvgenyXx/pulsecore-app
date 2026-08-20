package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class PlayerNameAlreadyExistsException extends BaseException {
    public PlayerNameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Игрок с таким именем уже существует");
    }
}