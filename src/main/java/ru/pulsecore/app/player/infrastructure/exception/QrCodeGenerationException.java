package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class QrCodeGenerationException extends BaseException {
    public QrCodeGenerationException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось сгенерировать QR-код");
    }
}