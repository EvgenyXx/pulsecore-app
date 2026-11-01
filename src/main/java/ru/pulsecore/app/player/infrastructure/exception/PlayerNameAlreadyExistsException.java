package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class PlayerNameAlreadyExistsException extends BaseException {
    public PlayerNameAlreadyExistsException() {
        super(
            HttpStatus.CONFLICT,
            "Это имя уже используется. Если это вы — напишите в Telegram.",
            "PLAYER_NAME_EXISTS",
            "https://t.me/@Evgeny_666"
        );
    }
}