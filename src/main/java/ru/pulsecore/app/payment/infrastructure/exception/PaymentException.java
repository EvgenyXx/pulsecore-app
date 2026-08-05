package ru.pulsecore.app.payment.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class PaymentException extends BaseException {
    public PaymentException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}