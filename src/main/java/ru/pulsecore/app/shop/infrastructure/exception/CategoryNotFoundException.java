package ru.pulsecore.app.shop.infrastructure.exception;

import org.springframework.http.HttpStatus;
import ru.pulsecore.app.shared.exception.BaseException;

public class CategoryNotFoundException extends BaseException {


    public CategoryNotFoundException() {
        super(HttpStatus.NOT_FOUND,"Категория не найдена");
    }
}
