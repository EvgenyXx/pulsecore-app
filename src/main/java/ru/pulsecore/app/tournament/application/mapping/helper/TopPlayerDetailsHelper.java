package ru.pulsecore.app.tournament.application.mapping.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.api.dto.response.DetailPeriod;
import ru.pulsecore.app.tournament.domain.entity.TopPlayersView;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentResultRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerMatchStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerTopStatsProjection;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopPlayerDetailsHelper {

    private final TournamentResultRepository resultRepository;
    private final TournamentMatchRepository matchRepository;

    public Map<UUID, DetailPeriod> prepareDetails(List<TopPlayersView> players, String period) {
        if (players == null || players.isEmpty()) return Map.of();

        LocalDate start = getStartDate(period);
        LocalDate end = LocalDate.now();

        Set<UUID> playerIds = players.stream()
                .map(TopPlayersView::getPlayerId)
                .collect(Collectors.toSet());

        List<String> playerNames = players.stream()
                .map(p -> p.getName().toLowerCase())
                .toList();

        Map<UUID, PlayerTopStatsProjection> moneyStats = fetchMoneyStats(playerIds, start, end);
        Map<String, PlayerMatchStatsProjection> matchStats = fetchMatchStats(playerNames, start, end);

        return players.stream().collect(Collectors.toMap(
                TopPlayersView::getPlayerId,
                view -> buildDetailPeriod(view, moneyStats, matchStats)
        ));
    }

    private Map<UUID, PlayerTopStatsProjection> fetchMoneyStats(Set<UUID> ids, LocalDate start, LocalDate end) {
        return resultRepository.getStatsForPlayers(ids, start, end).stream()
                .collect(Collectors.toMap(PlayerTopStatsProjection::getPlayerId, Function.identity()));
    }

    private Map<String, PlayerMatchStatsProjection> fetchMatchStats(List<String> names, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();

        return matchRepository.getMatchStatsByPlayerNames(names, startDateTime, endDateTime).stream()
                .collect(Collectors.toMap(
                        p -> p.getPlayerName().toLowerCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    private DetailPeriod buildDetailPeriod(
            TopPlayersView view,
            Map<UUID, PlayerTopStatsProjection> moneyStats,
            Map<String, PlayerMatchStatsProjection> matchStats) {

        PlayerTopStatsProjection money = moneyStats.get(view.getPlayerId());
        PlayerMatchStatsProjection matches = matchStats.get(view.getName().toLowerCase());

        long totalMatches = matches != null && matches.getTotalMatches() != null ? matches.getTotalMatches() : 0L;
        long wins = matches != null && matches.getWins() != null ? matches.getWins() : 0L;
        long loses = Math.max(0, totalMatches - wins);

        return new DetailPeriod(
                money != null ? money.getTotalAmount() : 0.0,
                money != null ? money.getAverageAmount() : 0.0,
                money != null ? money.getTournaments() : 0L,
                totalMatches,
                wins,
                loses
        );
    }

    private LocalDate getStartDate(String period) {
        LocalDate now = LocalDate.now();
        return switch (period.toUpperCase()) {
            case "MONTH" -> now.withDayOfMonth(1);
            case "YEAR" -> now.withDayOfYear(1);
            default -> now.with(DayOfWeek.MONDAY);
        };
    }


}