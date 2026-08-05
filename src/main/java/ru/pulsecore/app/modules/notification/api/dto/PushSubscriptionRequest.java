package ru.pulsecore.app.modules.notification.api.dto;

public record PushSubscriptionRequest(
        String endpoint,
        String p256dh,
        String auth
) {}