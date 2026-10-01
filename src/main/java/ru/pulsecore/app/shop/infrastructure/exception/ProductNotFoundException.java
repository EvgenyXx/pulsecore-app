package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(Long productId) {
        super(HttpStatus.NOT_FOUND,"Продукт не найден:" + productId);
    }
}
