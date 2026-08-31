package ru.pulsecore.app.tournament.infrastructure.exception;


import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class TournamentNotFoundException extends BaseException {
    public TournamentNotFoundException(String link) {
        super(HttpStatus.NOT_FOUND, "Турнир не найден: " + link);
    }
}