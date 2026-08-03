package ru.pulsecore.app.modules.player_modeles.api.dto.response;

import lombok.Builder;

@Builder
public record SubscriptionStatusResponse(boolean active, String expiresAt, String startedAt) {}