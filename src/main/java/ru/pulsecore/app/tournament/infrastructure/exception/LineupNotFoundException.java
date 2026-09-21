package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class LineupNotFoundException extends BaseException {
    public LineupNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Состав не найден: " + id);
    }
}