package ru.pulsecore.app.modules.push;

public record PushSubscriptionRequest(
        String endpoint,
        String p256dh,
        String auth
) {}