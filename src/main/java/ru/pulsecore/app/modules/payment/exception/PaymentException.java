package ru.pulsecore.app.modules.payment.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.modules.shared.exception.BaseException;

public class PaymentException extends BaseException {
    public PaymentException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}