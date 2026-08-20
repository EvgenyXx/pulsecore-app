package ru.pulsecore.app.tournament.application.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.DailyIncomeResponse;
import ru.pulsecore.app.player.api.dto.response.MonthlyIncomeResponse;

import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.mapper.AnalyticsMapper;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.DailyIncomeProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.MonthlyIncomeProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentIncomeService {

    private static final LocalDate EPOCH = LocalDate.of(2000, 1, 1);

    private final TournamentResultRepository repository;
    private final AnalyticsMapper mapper;
    private final PlayerClient playerClient;

    public MonthlyIncomeResponse getMonthlyIncome(UUID playerId, int year) {
        var player = playerClient.getPlayerById(playerId);
        List<MonthlyIncomeProjection> data = repository.getMonthlyIncome(player.playerId(), EPOCH, year);
        List<MonthlyIncomeResponse.MonthStat> stats = mapper.toMonthStats(data);


        double avg = stats.stream()
                .mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal)
                .average()
                .orElse(0);

        return MonthlyIncomeResponse.builder()
                .playerName(player.playerName())
                .months(stats)
                .overallAverage(avg)
                .build();
    }

    public DailyIncomeResponse getDailyIncome(UUID playerId, int year, int month) {
        var player = playerClient.getPlayerById(playerId);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<DailyIncomeProjection> data = repository.getDailyIncome(player.playerId(), start, end);
        List<DailyIncomeResponse.DayStat> stats = buildDayStats(data, end.getDayOfMonth());

        double monthTotal = stats.stream().mapToDouble(DailyIncomeResponse.DayStat::getTotal).sum();
        long daysWithEarnings = stats.stream().filter(s -> s.getCount() > 0).count();

        return DailyIncomeResponse.builder()
                .playerName(player.playerName())
                .year(year)
                .month(month)
                .days(stats)
                .monthTotal(monthTotal)
                .dailyAverage(daysWithEarnings > 0 ? monthTotal / daysWithEarnings : 0)
                .build();
    }

    private List<DailyIncomeResponse.DayStat> buildDayStats(List<DailyIncomeProjection> data, int daysInMonth) {
        Map<Integer, DailyIncomeProjection> map = data.stream()
                .collect(Collectors.toMap(DailyIncomeProjection::getDay, p -> p));

        return IntStream.rangeClosed(1, daysInMonth)
                .mapToObj(d -> {
                    DailyIncomeProjection p = map.get(d);
                    return DailyIncomeResponse.DayStat.builder()
                            .day(d)
                            .total(p != null ? p.getTotal() : 0.0)
                            .count(p != null ? p.getCount() : 0)
                            .build();
                })
                .toList();
    }


}