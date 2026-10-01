package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
        @NotNull(message = "quantity обязателен")
        @Min(value = 1, message = "Количество должно быть больше 0")
        Integer quantity
) {}