package ru.pulsecore.app.modules.player.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class SubscriptionRequiredException extends BaseException {
    public SubscriptionRequiredException() {
        super(HttpStatus.PAYMENT_REQUIRED, "Требуется активная подписка");
    }
}