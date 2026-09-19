package ru.pulsecore.app.notification.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PushSubscriptionRequest(
        String endpoint,
        String p256dh,
        String auth
) {}