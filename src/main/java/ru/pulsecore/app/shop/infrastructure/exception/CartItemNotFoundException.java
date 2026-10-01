package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class CartItemNotFoundException extends BaseException {

    public CartItemNotFoundException(Long itemId) {
        super(HttpStatus.NOT_FOUND, "Товар не найден в корзине: " + itemId);
    }
}