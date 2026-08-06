package ru.pulsecore.app.tournament.application.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.AnalyticsResponse;
import ru.pulsecore.app.tournament.infrastructure.persistence.mapper.AnalyticsMapper;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.LeagueStatProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueAnalyticsService {

    private final TournamentResultRepository tournamentResultRepository;
    private final AnalyticsMapper mapper;

    public AnalyticsResponse getAnalytics(UUID playerId, int days) {
        LocalDate since = LocalDate.now().minusDays(days);

        List<LeagueStatProjection> leagueStats = tournamentResultRepository.getAllLeaguesStats(since);
        double playerAverage = tournamentResultRepository.getPlayerAverage(playerId, since);

        List<AnalyticsResponse.LeagueStat> leagueStatDtos = mapper.toLeagueStats(leagueStats);

        double overallAverage = calculateOverallAverage(leagueStats);
        ClosestLeague closest = findClosestLeague(leagueStatDtos, playerAverage);

        return AnalyticsResponse.builder()
                .leagueStats(leagueStatDtos)
                .overallAverage(overallAverage)
                .playerAverage(playerAverage)
                .closestLeague(closest.name())
                .closestDifference(closest.difference())
                .build();
    }

    private double calculateOverallAverage(List<LeagueStatProjection> stats) {
        int totalCount = stats.stream().mapToInt(s -> s.getCount().intValue()).sum();
        double overallTotal = stats.stream().mapToDouble(LeagueStatProjection::getSum).sum();
        return totalCount > 0 ? overallTotal / totalCount : 0;
    }

    private ClosestLeague findClosestLeague(List<AnalyticsResponse.LeagueStat> stats, double playerAverage) {
        return stats.stream()
                .map(ls -> new ClosestLeague(
                        ls.getLeague(),
                        Math.abs(ls.getAverageAmount() - playerAverage)))
                .min(java.util.Comparator.comparingDouble(ClosestLeague::difference))
                .orElse(new ClosestLeague(null, 0));
    }

    private record ClosestLeague(String name, double difference) {}
}