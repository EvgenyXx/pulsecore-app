package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Заказ не найден: " + id);
    }
}