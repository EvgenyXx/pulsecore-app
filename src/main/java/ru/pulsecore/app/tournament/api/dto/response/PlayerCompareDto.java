package ru.pulsecore.app.tournament.api.dto.response;

import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerCompareResponse;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.util.UUID;

public record PlayerCompareDto(
        UUID playerId,
        String playerName,
        String primaryLeague,
        Long tournaments,
        Double totalAmount,
        Double averageAmount
) {
    public static PlayerCompareDto from(PlayerCompareResponse p) {
        return new PlayerCompareDto(
                p.getPlayerId(),
                StringUtils.capitalize(p.getPlayerName()),
                p.getPrimaryLeague(),
                p.getTournaments(),
                p.getTotalAmount(),
                p.getAverageAmount()
        );
    }
}