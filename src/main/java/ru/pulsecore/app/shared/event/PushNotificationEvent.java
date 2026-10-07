package ru.pulsecore.app.shared.event;

import java.util.Map;
import java.util.UUID;

public record PushNotificationEvent(
        Map<UUID, PushContent> contentByPlayer
) {

}