package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.pulsecore.app.shop.domain.PaymentStatus;

public record UpdatePaymentStatusRequest(

        @NotNull(message = "status обязателен")
        PaymentStatus paymentStatus
) {
}
