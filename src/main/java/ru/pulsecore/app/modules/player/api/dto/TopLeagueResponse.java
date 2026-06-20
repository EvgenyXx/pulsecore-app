package ru.pulsecore.app.modules.player.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopLeagueResponse  implements Serializable {
    private List<TopPlayerDto> top5;
    private int playerPosition;
    private int playerTournaments;
}