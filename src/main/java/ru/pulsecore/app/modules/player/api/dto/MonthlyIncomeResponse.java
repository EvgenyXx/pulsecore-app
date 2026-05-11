package ru.pulsecore.app.modules.player.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MonthlyIncomeResponse {
    private String playerName;
    private List<MonthStat> months;
    private double overallAverage;

    @Data
    @Builder
    public static class MonthStat {
        private String month;
        private double total;
        private int count;
        private double average;
    }
}