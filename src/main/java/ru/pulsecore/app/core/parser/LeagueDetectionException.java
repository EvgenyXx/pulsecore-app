package ru.pulsecore.app.core.parser;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class LeagueDetectionException extends BaseException {
    public LeagueDetectionException(String title) {
        super(HttpStatus.BAD_REQUEST, "Не удалось определить лигу: " + title);
    }
}