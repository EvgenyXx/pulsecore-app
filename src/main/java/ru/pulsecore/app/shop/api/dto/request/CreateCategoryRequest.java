package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

        @NotBlank(message = "Название обязательно")
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String name
) {}