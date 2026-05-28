package ru.pulsecore.app.modules.lineup.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineupDto {
    private String time;
    private String league;
    private String hall;
    private String players;
    private String date;
    private boolean isPlayer;
}