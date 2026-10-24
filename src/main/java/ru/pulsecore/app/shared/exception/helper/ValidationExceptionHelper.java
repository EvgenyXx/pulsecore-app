package ru.pulsecore.app.shared.exception.helper;

import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@UtilityClass
public class ValidationExceptionHelper {

    public static String extractMessage(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .reduce((a, b) -> a + "; " + b)
                .orElse("Ошибка валидации");
    }
}