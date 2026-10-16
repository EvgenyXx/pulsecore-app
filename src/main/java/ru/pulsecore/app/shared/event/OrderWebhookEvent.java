package ru.pulsecore.app.shared.event;

import java.math.BigDecimal;

public record OrderWebhookEvent(
        Long orderId,
        YookassaEventType eventType,
        BigDecimal amount,
        String currency
) {}