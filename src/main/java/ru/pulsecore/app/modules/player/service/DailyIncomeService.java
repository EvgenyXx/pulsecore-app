package ru.pulsecore.app.modules.player.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.modules.player.api.dto.DailyIncomeResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.api.dto.DailyIncomeProjection;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyIncomeService {

    private final PlayerService playerService;
    private final TournamentResultRepository tournamentResultRepository;

    public DailyIncomeResponse getDailyIncome(UUID playerId, int year, int month) {
        Player player = playerService.getById(playerId);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<DailyIncomeProjection> data = tournamentResultRepository.getDailyIncome(player, start, end);

        Map<Integer, DailyIncomeProjection> map = data.stream()
                .collect(Collectors.toMap(DailyIncomeProjection::getDay, p -> p));

        int daysInMonth = end.getDayOfMonth();
        List<DailyIncomeResponse.DayStat> stats = new ArrayList<>();
        double monthTotal = 0;
        int daysWithEarnings = 0;

        for (int d = 1; d <= daysInMonth; d++) {
            DailyIncomeProjection p = map.get(d);
            double total = p != null ? p.getTotal() : 0.0;
            int count = p != null ? p.getCount() : 0;
            monthTotal += total;
            if (count > 0) daysWithEarnings++;
            stats.add(DailyIncomeResponse.DayStat.builder()
                    .day(d).total(total).count(count).build());
        }

        return DailyIncomeResponse.builder()
                .playerName(player.getName())
                .year(year).month(month)
                .days(stats)
                .monthTotal(monthTotal)
                .dailyAverage(daysWithEarnings > 0 ? monthTotal / daysWithEarnings : 0)
                .build();
    }
}