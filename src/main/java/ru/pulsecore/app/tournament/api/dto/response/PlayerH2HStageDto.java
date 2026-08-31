package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.pulsecore.app.tournament.domain.entity.MatchStage;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerH2HProjection;

@Data
@Builder
public class PlayerH2HStageDto {
    private MatchStage stage;
    private Long totalMatches;
    private Long player1Wins;
    private Long player2Wins;

    public static PlayerH2HStageDto from(PlayerH2HProjection p) {
        return PlayerH2HStageDto.builder()
                .stage(p.getStage())
                .totalMatches(p.getTotalMatches() != null ? p.getTotalMatches() : 0L)
                .player1Wins(p.getPlayer1Wins() != null ? p.getPlayer1Wins() : 0L)
                .player2Wins(p.getPlayer2Wins() != null ? p.getPlayer2Wins() : 0L)
                .build();
    }
}