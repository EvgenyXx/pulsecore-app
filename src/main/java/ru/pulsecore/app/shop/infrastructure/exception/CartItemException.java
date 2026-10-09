package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class CartItemException extends BaseException {

    public CartItemException(Integer stock) {
        super(HttpStatus.BAD_REQUEST, "Максимум " + stock);
    }

    public CartItemException(){
        super(HttpStatus.BAD_REQUEST,"Минимум 1 шт");
    }
}