package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class TournamentResultNotFoundException extends BaseException {
    public TournamentResultNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Результат турнира не найден: " + id);
    }
}