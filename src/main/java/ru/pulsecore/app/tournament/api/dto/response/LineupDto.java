package ru.pulsecore.app.tournament.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineupDto {
    private String time;
    private String league;
    private String hall;
    private String players;
    private LocalDate date;
    private boolean isPlayer;
    private String link;
    private String type;
}