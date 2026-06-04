// ==================== 4. DashboardService.java ====================
package ru.pulsecore.app.modules.player.service.analytic.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.player.api.dto.DashboardResponse;
import ru.pulsecore.app.modules.player.api.dto.LastResultDto;
import ru.pulsecore.app.modules.player.api.dto.SubscriptionInfoDto;
import ru.pulsecore.app.modules.player.api.dto.UpcomingLineupDto;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.league.LeagueService;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PlayerService playerService;
    private final TournamentResultRepository tournamentResultRepository;
    private final LineupRepository lineupRepository;
    private final LeagueService leagueService;

    public DashboardResponse getDashboard(UUID id) {
        Player player = playerService.getById(id);
        String playerNameLower = player.getName().toLowerCase();

        String primaryLeague = leagueService.getPrimaryLeague(id);

        var lastResult = tournamentResultRepository.findTopByPlayerOrderByDateDesc(player)
                .map(r -> LastResultDto.builder()
                        .date(r.getDate().toString()).amount(r.getAmount())
                        .tournamentLink(r.getTournament().getLink()).build())
                .orElse(null);

        LocalDate today = LocalDate.now();
        List<Lineup> lineups = lineupRepository.findByDateBetweenOrderByDateAscTimeAsc(today, today.plusDays(2));
        Map<LocalDate, List<Lineup>> byDate = lineups.stream()
                .collect(Collectors.groupingBy(Lineup::getDate, LinkedHashMap::new, Collectors.toList()));
        LocalDate soonestDate = byDate.keySet().stream().min(LocalDate::compareTo).orElse(null);
        List<UpcomingLineupDto> upcomingLineups = new ArrayList<>();

        for (Map.Entry<LocalDate, List<Lineup>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Lineup> dayLineups = entry.getValue();
            List<Lineup> myLineups = dayLineups.stream()
                    .filter(l -> l.getPlayers().toLowerCase().contains(playerNameLower)).toList();
            if (!myLineups.isEmpty()) {
                for (Lineup lineup : myLineups) {
                    upcomingLineups.add(UpcomingLineupDto.builder()
                            .date(lineup.getDate().toString()).time(lineup.getTime())
                            .league(lineup.getLeague()).inLineup(true)
                            .players(capitalize(lineup.getPlayers())).isSoon(date.equals(soonestDate)).build());
                }
            } else {
                upcomingLineups.add(UpcomingLineupDto.builder()
                        .date(date.toString()).time(null).league(null)
                        .inLineup(false).players(null).isSoon(date.equals(soonestDate)).build());
            }
        }

        var sub = player.getSubscription();
        SubscriptionInfoDto subInfo = sub != null && sub.isActiveNow()
                ? SubscriptionInfoDto.builder().active(true).expiresAt(sub.getExpiresAt().toString()).build()
                : SubscriptionInfoDto.builder().active(false).build();

        return DashboardResponse.builder()
                .playerName(capitalize(player.getName())).lastResult(lastResult)
                .upcomingLineups(upcomingLineups).subscription(subInfo)

                .primaryLeague(primaryLeague).build();
    }

    private String capitalize(String name) {
        if (name == null || name.isBlank()) return name;
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}