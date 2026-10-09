package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ValidateOrderRequest(
        @NotEmpty(message = "Список товаров не может быть пустым")
        List<Long> itemIds
) {}