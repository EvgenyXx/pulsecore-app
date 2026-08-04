package ru.pulsecore.app.modules.tournament_module.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class TournamentParseException extends BaseException {
    public TournamentParseException(String url, Exception cause) {
        super(HttpStatus.BAD_REQUEST, "Ошибка парсинга турнира: " + cause.getMessage());
    }
}