package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoreStatsDto {
    private String score;
    private Long player1Wins;
    private Long player2Wins;
}