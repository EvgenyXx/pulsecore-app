package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.pulsecore.app.shop.domain.DeliveryMethod;
import ru.pulsecore.app.shop.domain.PaymentMethod;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty List<Long> itemIds,

        @Size(max = 100) String customerFirstName,
        @Size(max = 100) String customerLastName,
        @Size(max = 100) String customerMiddleName,

        @NotBlank @Size(max = 30) String phone,

        @Size(max = 100) String city,
        @Size(max = 300) String street,
        @Size(max = 2000) String comment,

        @NotNull PaymentMethod paymentMethod,
        @NotNull DeliveryMethod deliveryMethod
) {}