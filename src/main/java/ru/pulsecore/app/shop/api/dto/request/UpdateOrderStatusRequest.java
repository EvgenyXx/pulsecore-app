package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.pulsecore.app.shop.domain.OrderStatus;

public record UpdateOrderStatusRequest(
        @NotNull(message = "status обязателен")
        OrderStatus status
) {}