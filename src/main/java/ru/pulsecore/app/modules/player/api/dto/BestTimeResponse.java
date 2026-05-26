package ru.pulsecore.app.modules.player.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestTimeResponse {
    private String time;
    private Long gamesCount;
    private Double avgPoints;
    private Double totalPoints;

}