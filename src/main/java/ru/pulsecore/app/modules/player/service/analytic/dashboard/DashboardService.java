package ru.pulsecore.app.modules.player.service.analytic.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.player.api.dto.dashboard.DashboardResponse;
import ru.pulsecore.app.modules.player.api.dto.dashboard.LastResultDto;
import ru.pulsecore.app.modules.player.api.dto.dashboard.SubscriptionInfoDto;
import ru.pulsecore.app.modules.player.api.dto.dashboard.UpcomingLineupDto;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.league.LeagueService;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.util.StringUtils;
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

        return DashboardResponse.builder()
                .playerName(StringUtils.capitalize(player.getName()))
                .lastResult(getLastResult(player))
                .upcomingLineups(getUpcomingLineups(player))
                .subscription(getSubscriptionInfo(player))
                .primaryLeague(leagueService.getPrimaryLeague(id))
                .build();
    }

    private LastResultDto getLastResult(Player player) {
        return tournamentResultRepository.findTopByPlayerOrderByDateDesc(player)
                .map(r -> LastResultDto.builder()
                        .date(r.getDate().toString())
                        .amount(r.getAmount())
                        .tournamentLink(r.getTournament().getLink())
                        .build())
                .orElse(null);
    }

    private List<UpcomingLineupDto> getUpcomingLineups(Player player) {
        String playerNameLower = player.getName().toLowerCase();
        LocalDate today = LocalDate.now();
        List<Lineup> lineups = lineupRepository.findByDateBetweenOrderByDateAscTimeAsc(today, today.plusDays(2));

        Map<LocalDate, List<Lineup>> byDate = lineups.stream()
                .collect(Collectors.groupingBy(Lineup::getDate, LinkedHashMap::new, Collectors.toList()));
        LocalDate soonestDate = byDate.keySet().stream().min(LocalDate::compareTo).orElse(null);

        List<UpcomingLineupDto> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Lineup>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Lineup> myLineups = entry.getValue().stream()
                    .filter(l -> l.getPlayers().toLowerCase().contains(playerNameLower))
                    .toList();

            if (myLineups.isEmpty()) {
                result.add(UpcomingLineupDto.builder()
                        .date(date.toString())
                        .inLineup(false)
                        .isSoon(date.equals(soonestDate))
                        .build());
            } else {
                myLineups.forEach(lineup -> result.add(UpcomingLineupDto.builder()
                        .date(lineup.getDate().toString())
                        .time(lineup.getTime())
                        .league(lineup.getLeague())
                        .inLineup(true)
                        .players(StringUtils.capitalize(lineup.getPlayers()))
                        .isSoon(date.equals(soonestDate))
                        .build()));
            }
        }
        return result;
    }

    private SubscriptionInfoDto getSubscriptionInfo(Player player) {
        var sub = player.getSubscription();
        if (sub != null && sub.isActiveNow()) {
            return SubscriptionInfoDto.builder()
                    .active(true)
                    .expiresAt(sub.getExpiresAt().toString())
                    .build();
        }
        return SubscriptionInfoDto.builder().active(false).build();
    }
}