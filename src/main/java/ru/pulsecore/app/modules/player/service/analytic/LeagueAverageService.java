// AnalyticsService.java
package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.api.dto.AnalyticsResponse;
import ru.pulsecore.app.modules.tournament.api.dto.LeagueStatProjection;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor//todo добавить мапперы в контроллеры
public class LeagueAverageService {

    private final TournamentResultRepository tournamentResultRepository;

    public AnalyticsResponse getAnalytics(UUID playerId, int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        List<LeagueStatProjection> stats = tournamentResultRepository.getAllLeaguesStats(since);
        double playerAverage = tournamentResultRepository.getPlayerAverage(playerId, since);

        if (stats.isEmpty()) {
            return AnalyticsResponse.builder()
                    .leagueStats(List.of())
                    .overallAverage(0)
                    .playerAverage(playerAverage)
                    .closestLeague(null)
                    .closestDifference(0)
                    .build();
        }

        List<AnalyticsResponse.LeagueStat> leagueStats = stats.stream()
                .map(p -> AnalyticsResponse.LeagueStat.builder()
                        .league(p.getLeague())
                        .tournamentCount(p.getCount().intValue())
                        .totalAmount(p.getSum())
                        .averageAmount(p.getAvg())
                        .build())
                .toList();

        int totalCount = stats.stream().mapToInt(s -> s.getCount().intValue()).sum();
        double overallTotal = stats.stream().mapToDouble(LeagueStatProjection::getSum).sum();

        String closestLeague = null;
        double closestDiff = Double.MAX_VALUE;
        for (var ls : leagueStats) {
            double diff = Math.abs(ls.getAverageAmount() - playerAverage);
            if (diff < closestDiff) {
                closestDiff = diff;
                closestLeague = ls.getLeague();
            }
        }

        return AnalyticsResponse.builder()
                .leagueStats(leagueStats)
                .overallAverage(totalCount > 0 ? overallTotal / totalCount : 0)
                .playerAverage(playerAverage)
                .closestLeague(closestLeague)
                .closestDifference(closestDiff)
                .build();
    }
}