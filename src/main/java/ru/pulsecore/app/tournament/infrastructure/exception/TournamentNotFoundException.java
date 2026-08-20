package ru.pulsecore.app.tournament.infrastructure.exception;


import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class TournamentNotFoundException extends BaseException {
    public TournamentNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Турнир не найден: " + id);
    }
}