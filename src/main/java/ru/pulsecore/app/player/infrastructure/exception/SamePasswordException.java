package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class SamePasswordException extends BaseException {
    public SamePasswordException() {
        super(HttpStatus.BAD_REQUEST, "Новый пароль не должен совпадать со старым");
    }
}