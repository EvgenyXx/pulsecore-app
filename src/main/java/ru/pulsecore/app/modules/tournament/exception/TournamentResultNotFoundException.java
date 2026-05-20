package ru.pulsecore.app.modules.tournament.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class TournamentResultNotFoundException extends BaseException {
    public TournamentResultNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Результат турнира не найден: " + id);
    }
}