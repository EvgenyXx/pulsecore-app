package ru.pulsecore.app.tournament.infrastructure.exception;

import ru.pulsecore.app.shared.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TournamentProcessException extends BaseException {
    public TournamentProcessException(String link, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось обработать турнир: " + link);
        initCause(cause);
    }
}