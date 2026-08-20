package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class TournamentParseException extends BaseException {
    public TournamentParseException( Exception cause) {
        super(HttpStatus.BAD_REQUEST, "Ошибка парсинга турнира: " + cause.getMessage());
    }
}