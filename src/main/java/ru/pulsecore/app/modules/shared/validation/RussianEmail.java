package ru.pulsecore.app.modules.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RussianEmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RussianEmail {

    String message() default "Используйте российский почтовый домен (mail.ru, yandex.ru, rambler.ru и др.)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}