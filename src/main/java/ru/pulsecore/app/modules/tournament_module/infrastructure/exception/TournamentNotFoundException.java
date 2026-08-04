package ru.pulsecore.app.modules.tournament_module.infrastructure.exception;


import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class TournamentNotFoundException extends BaseException {
    public TournamentNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Турнир не найден: " + id);
    }
}