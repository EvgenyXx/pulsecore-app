package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class RoleNotGrantedException extends BaseException {
    public RoleNotGrantedException(String roleName) {
        super(HttpStatus.NOT_FOUND, "У игрока нет роли: " + roleName);
    }
}