package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class RoleNotGrantedException extends BaseException {
    public RoleNotGrantedException(String roleName) {
        super(HttpStatus.NOT_FOUND, "У игрока нет роли: " + roleName);
    }
}