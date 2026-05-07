package ru.pulsecore.app.modules.player.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TopWeekResponse {
    private String playerName;
    private int playerPosition;
    private double playerTotal;
    private long playerTournaments;
    private String title;
    private boolean hasCrown;
    private List<TopPlayer> top5;

    @Data
    @Builder
    public static class TopPlayer {
        private String name;
        private Double total;
        private Long tournaments;
    }
}