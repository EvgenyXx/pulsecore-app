package ru.pulsecore.app.tournament.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class PlayerStatsNotFoundException extends BaseException {
    public PlayerStatsNotFoundException(String name) {
        super(HttpStatus.NOT_FOUND, "У игрока " + name + " нет данных за выбранный период");
    }
}