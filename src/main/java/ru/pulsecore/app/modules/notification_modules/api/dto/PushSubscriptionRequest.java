package ru.pulsecore.app.modules.notification_modules.api.dto;

public record PushSubscriptionRequest(
        String endpoint,
        String p256dh,
        String auth
) {}