package ru.pulsecore.app.shared.dto;

import lombok.Builder;

@Builder
public record SubscriptionStatusResponse(boolean active, String expiresAt, String startedAt) {}