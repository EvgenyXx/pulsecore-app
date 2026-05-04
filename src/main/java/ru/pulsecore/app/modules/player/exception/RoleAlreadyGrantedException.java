package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class RoleAlreadyGrantedException extends BaseException {
    public RoleAlreadyGrantedException(String roleName) {
        super(HttpStatus.CONFLICT, "Игрок уже имеет роль: " + roleName);
    }
}