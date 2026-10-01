package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;




public class CartNotFoundException extends BaseException {

    public CartNotFoundException(){
        super(HttpStatus.NOT_FOUND,"Не удалось найти коризну");
    }

}
