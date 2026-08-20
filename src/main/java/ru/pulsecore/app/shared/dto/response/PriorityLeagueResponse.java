package ru.pulsecore.app.shared.dto.response;

import java.util.UUID;

public record PriorityLeagueResponse(UUID playerId, String league) {}