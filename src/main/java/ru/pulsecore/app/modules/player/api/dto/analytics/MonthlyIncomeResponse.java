package ru.pulsecore.app.modules.player.api.dto.analytics;

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
public class MonthlyIncomeResponse implements Serializable {
    private String playerName;
    private List<MonthStat> months;
    private double overallAverage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthStat implements  Serializable {
        private String month;
        private double total;
        private int count;
        private double average;
    }
}