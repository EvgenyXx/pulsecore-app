package ru.pulsecore.app.shared.dto.response;

import ru.pulsecore.app.shared.event.YookassaEventType;

public record OrderYookassaEvent(

        Long orderId,
        YookassaEventType eventType,
        String yookassaPaymentId

        )

{}