package ru.pulsecore.app.shared.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionWebhookEvent(
        UUID playerId,
        YookassaEventType eventType,
        String paymentId,
        int month,
        BigDecimal amount,
        String currency,
        LocalDateTime paidAt
) {}