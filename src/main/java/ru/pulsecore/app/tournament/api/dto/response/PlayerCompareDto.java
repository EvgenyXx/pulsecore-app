package ru.pulsecore.app.tournament.api.dto.response;

import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerCompareResponse;
import ru.pulsecore.app.shared.util.PlayerNameUtils;

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
                PlayerNameUtils.capitalize(p.getPlayerName()),
                p.getPrimaryLeague(),
                p.getTournaments(),
                p.getTotalAmount(),
                p.getAverageAmount()
        );
    }
}