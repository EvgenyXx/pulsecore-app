package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class PageNotFoundException extends BaseException {
    public PageNotFoundException(String url) {
        super(HttpStatus.NOT_FOUND, "Страница не найдена: " + url);
    }
}