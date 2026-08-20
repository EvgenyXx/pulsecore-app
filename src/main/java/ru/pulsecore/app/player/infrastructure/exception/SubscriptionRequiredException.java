package ru.pulsecore.app.player.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class SubscriptionRequiredException extends BaseException {
    public SubscriptionRequiredException() {
        super(HttpStatus.PAYMENT_REQUIRED, "Требуется активная подписка");
    }
}