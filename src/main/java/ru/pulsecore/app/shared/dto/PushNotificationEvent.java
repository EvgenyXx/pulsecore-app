package ru.pulsecore.app.shared.dto;

import java.util.UUID;

public record PushNotificationEvent(
        UUID playerId,
        String title,
        String body,
        String url
) {
}