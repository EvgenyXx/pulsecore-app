package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;

@Builder
public record PlayerMatchStatsDto(
        String playerName,
        Double groupWinPercent,
        Double semifinalWinPercent,
        Double thirdPlaceWinPercent,
        Double finalWinPercent
) {}