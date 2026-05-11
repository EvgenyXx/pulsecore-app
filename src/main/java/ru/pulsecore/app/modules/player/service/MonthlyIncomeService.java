package ru.pulsecore.app.modules.player.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.api.dto.MonthlyIncomeResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.api.dto.MonthlyIncomeProjection;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonthlyIncomeService {

    private final PlayerService playerService;
    private final TournamentResultRepository repository;

    public MonthlyIncomeResponse getMonthlyIncome(UUID playerId, int year) {
        Player player = playerService.getById(playerId);
        LocalDate since = LocalDate.of(2000, 1, 1);
        List<MonthlyIncomeProjection> data = repository.getMonthlyIncome(player, since, year);

        List<MonthlyIncomeResponse.MonthStat> stats = data.stream()
                .map(p -> MonthlyIncomeResponse.MonthStat.builder()
                        .month(p.getMonth())
                        .total(p.getTotal())
                        .count(p.getCount().intValue())
                        .average(p.getAverage())
                        .build())
                .toList();

        double avg = stats.stream().mapToDouble(MonthlyIncomeResponse.MonthStat::getTotal).average().orElse(0);

        return MonthlyIncomeResponse.builder()
                .playerName(player.getName())
                .months(stats)
                .overallAverage(avg)
                .build();
    }
}