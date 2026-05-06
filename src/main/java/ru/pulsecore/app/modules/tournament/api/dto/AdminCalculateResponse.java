package ru.pulsecore.app.modules.tournament.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminCalculateResponse {
    private String playerName;
    private String startDate;
    private String endDate;
    private double totalAmount;
    private int tournamentCount;
    private List<TournamentResultItem> tournaments;

    @Data
    @Builder
    public static class TournamentResultItem {
        private String date;
        private double amount;
        private String tournamentTitle;
        private Long tournamentId;
        private boolean hasRemoved;
    }
}