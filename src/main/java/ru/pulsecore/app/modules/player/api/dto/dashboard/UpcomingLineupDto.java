package ru.pulsecore.app.modules.player.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingLineupDto implements Serializable {
    private String date;
    private String time;
    private String league;
    private boolean inLineup;
    private String players;
    private boolean isSoon;
}