package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class RoleNotFoundException extends BaseException {
    public RoleNotFoundException(String roleName) {
        super(HttpStatus.NOT_FOUND, "Роль не найдена: " + roleName);
    }
}