package ru.pulsecore.app.modules.player_modeles.api.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TournamentListResponse {
    private String playerName;
    private String start;
    private String end;
    private int count;
    private double sum;
    private List<TournamentResultItem> tournaments;

    @Data
    @Builder
    public static class TournamentResultItem {
        private String date;
        private Double amount;
        private Long resultId;
    }
}