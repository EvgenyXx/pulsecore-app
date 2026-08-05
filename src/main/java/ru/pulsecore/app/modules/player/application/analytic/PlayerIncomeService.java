package ru.pulsecore.app.modules.player.application.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.api.dto.response.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.api.dto.response.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.entity.Player;
import ru.pulsecore.app.modules.player.infrastructure.persistence.mapping.AnalyticsMapper;
import ru.pulsecore.app.modules.tournament.infrastructure.persistence.repository.projection.DailyIncomeProjection;
import ru.pulsecore.app.modules.tournament.infrastructure.persistence.repository.projection.MonthlyIncomeProjection;
import ru.pulsecore.app.modules.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PlayerIncomeService {
    //todo тоже перенести все в туринры
    private static final LocalDate EPOCH = LocalDate.of(2000, 1, 1);

    private final TournamentResultRepository repository;
    private final AnalyticsMapper mapper;

    public MonthlyIncomeResponse getMonthlyIncome(Player player, int year) {
        List<MonthlyIncomeProjection> data = repository.getMonthlyIncome(player.getId(), EPOCH, year);
        List<MonthlyIncomeResponse.MonthStat> stats = mapper.toMonthStats(data);

        double avg = stats.stream()
                .mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal)
                .average()
                .orElse(0);

        return MonthlyIncomeResponse.builder()
                .playerName(player.getName())
                .months(stats)
                .overallAverage(avg)
                .build();
    }

    public DailyIncomeResponse getDailyIncome(Player player, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<DailyIncomeProjection> data = repository.getDailyIncome(player.getId(), start, end);
        List<DailyIncomeResponse.DayStat> stats = buildDayStats(data, end.getDayOfMonth());

        double monthTotal = stats.stream().mapToDouble(DailyIncomeResponse.DayStat::getTotal).sum();
        long daysWithEarnings = stats.stream().filter(s -> s.getCount() > 0).count();

        return DailyIncomeResponse.builder()
                .playerName(player.getName())
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