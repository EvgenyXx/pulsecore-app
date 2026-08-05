package ru.pulsecore.app.player.infrastructure.persistence.mapping;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.api.dto.response.AnalyticsResponse;
import ru.pulsecore.app.player.api.dto.response.MonthlyIncomeResponse;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.LeagueStatProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.MonthlyIncomeProjection;

import java.util.List;

@Component
public class AnalyticsMapper {

    public List<AnalyticsResponse.LeagueStat> toLeagueStats(List<LeagueStatProjection> projections) {
        return projections.stream()
                .map(p -> AnalyticsResponse.LeagueStat.builder()
                        .league(p.getLeague())
                        .tournamentCount(p.getCount().intValue())
                        .totalAmount(p.getSum())
                        .averageAmount(p.getAvg())
                        .build())
                .toList();
    }

    public List<MonthlyIncomeResponse.MonthStat> toMonthStats(List<MonthlyIncomeProjection> projections) {
        return projections.stream()
                .map(p -> MonthlyIncomeResponse.MonthStat.builder()
                        .month(p.getMonth())
                        .total(p.getTotal())
                        .count(p.getCount().intValue())
                        .average(p.getAverage())
                        .build())
                .toList();
    }
}