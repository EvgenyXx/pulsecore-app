package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerH2HSummaryProjection;

@Data
@Builder
public class PlayerH2HSummaryDto {
    private Long totalMatches;
    private Long player1Wins;
    private Long player2Wins;

    public static PlayerH2HSummaryDto from(PlayerH2HSummaryProjection p) {
        return PlayerH2HSummaryDto.builder()
                .totalMatches(p.getTotalMatches() != null ? p.getTotalMatches() : 0L)
                .player1Wins(p.getPlayer1Wins() != null ? p.getPlayer1Wins() : 0L)
                .player2Wins(p.getPlayer2Wins() != null ? p.getPlayer2Wins() : 0L)
                .build();
    }
}