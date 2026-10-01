package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class OrderException extends BaseException {

    public OrderException(String message){
        super(HttpStatus.NOT_FOUND,message);
    }
}
