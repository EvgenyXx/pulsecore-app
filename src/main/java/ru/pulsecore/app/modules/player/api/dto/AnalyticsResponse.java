// AnalyticsResponse.java
package ru.pulsecore.app.modules.player.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalyticsResponse {
    private List<LeagueStat> leagueStats;
    private double overallAverage;
    private double playerAverage;
    private String closestLeague;
    private double closestDifference;

    @Data
    @Builder
    public static class LeagueStat {
        private String league;
        private int tournamentCount;
        private double totalAmount;
        private double averageAmount;
    }
}