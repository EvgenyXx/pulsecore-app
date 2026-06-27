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
public class DailyIncomeResponse implements Serializable {
    private String playerName;
    private int year;
    private int month;
    private List<DayStat> days;
    private double monthTotal;
    private double dailyAverage;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayStat implements  Serializable {
        private int day;
        private double total;
        private int count;
    }
}