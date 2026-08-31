package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlayerH2HResponseDto {
    private String player1Name;
    private String player2Name;
    private PlayerH2HSummaryDto summary;
    private List<PlayerH2HStageDto> stages;
}