package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class MailStrategyNotFoundException extends BaseException {
    public MailStrategyNotFoundException(String type) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Почтовая стратегия не найдена: " + type);
    }
}