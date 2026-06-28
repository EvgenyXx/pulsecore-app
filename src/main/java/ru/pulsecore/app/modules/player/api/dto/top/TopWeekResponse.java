package ru.pulsecore.app.modules.player.api.dto.top;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TopWeekResponse {
    private String playerName;
    private int playerPosition;
    private List<TopPlayer> top5;

    @Data
    @Builder
    public static class TopPlayer {
        private String name;
        private Long tournaments;
        private String primaryLeague;
    }
}