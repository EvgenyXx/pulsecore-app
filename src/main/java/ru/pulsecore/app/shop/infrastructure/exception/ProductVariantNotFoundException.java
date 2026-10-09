package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class ProductVariantNotFoundException extends BaseException {

    public ProductVariantNotFoundException(Long productVariantId) {
        super(HttpStatus.NOT_FOUND, "Вариант не найден: " + productVariantId);
    }
}
