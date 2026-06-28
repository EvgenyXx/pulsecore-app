package ru.pulsecore.app.modules.player.api.dto.subscription;

import lombok.Builder;

@Builder
public record SubscriptionStatusResponse(boolean active, String expiresAt, String startedAt) {}