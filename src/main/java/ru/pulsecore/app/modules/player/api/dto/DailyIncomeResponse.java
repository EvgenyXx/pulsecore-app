package ru.pulsecore.app.modules.player.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DailyIncomeResponse {
    private String playerName;
    private int year;
    private int month;
    private List<DayStat> days;
    private double monthTotal;
    private double dailyAverage;

    @Data
    @Builder
    public static class DayStat {
        private int day;
        private double total;
        private int count;
    }
}