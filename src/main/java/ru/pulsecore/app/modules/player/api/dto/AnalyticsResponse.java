// AnalyticsResponse.java
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
public class AnalyticsResponse implements Serializable {
    private List<LeagueStat> leagueStats;
    private double overallAverage;
    private double playerAverage;
    private String closestLeague;
    private double closestDifference;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeagueStat implements Serializable {
        private String league;
        private int tournamentCount;
        private double totalAmount;
        private double averageAmount;
    }
}