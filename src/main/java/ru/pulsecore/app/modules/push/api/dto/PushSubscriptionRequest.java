package ru.pulsecore.app.modules.push.api.dto;

public record PushSubscriptionRequest(
        String endpoint,
        String p256dh,
        String auth
) {}