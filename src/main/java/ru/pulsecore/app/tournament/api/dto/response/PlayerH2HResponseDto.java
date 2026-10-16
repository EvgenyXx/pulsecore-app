package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerH2HResponseDto {
    private String player1Name;
    private String player2Name;
    private PlayerH2HSummaryDto summary;
}