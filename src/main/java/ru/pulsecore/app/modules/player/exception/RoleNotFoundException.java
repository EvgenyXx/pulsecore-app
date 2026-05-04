package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class RoleNotFoundException extends BaseException {
    public RoleNotFoundException(String roleName) {
        super(HttpStatus.NOT_FOUND, "Роль не найдена: " + roleName);
    }
}