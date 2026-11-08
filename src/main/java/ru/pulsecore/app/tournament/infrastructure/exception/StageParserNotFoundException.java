package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class StageParserNotFoundException extends BaseException {


    public StageParserNotFoundException() {
        super(HttpStatus.NOT_FOUND,"Не найдена стадия под данный формат туринра ");
    }
}
