package ru.pulsecore.app.tournament.application.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.player.api.dto.response.PlayerSummaryResponse;
import ru.pulsecore.app.player.api.dto.response.LastResultDto;
import ru.pulsecore.app.player.api.dto.response.SubscriptionInfoDto;
import ru.pulsecore.app.player.api.dto.response.UpcomingLineupDto;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.LineupRepository;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerSummaryService {


    private final PlayerClient  playerClient;
    private final TournamentResultRepository tournamentResultRepository;
    private final LineupRepository lineupRepository;


    @Cacheable(value = CacheNames.DASHBOARD,key = "#id")
    public PlayerSummaryResponse getDashboard(UUID id) {

        PlayerData player = playerClient.getPlayerById(id);

        return PlayerSummaryResponse.builder()
                .playerName(StringUtils.capitalize(player.playerName()))
                .lastResult(getLastResult(player.playerId()))
                .upcomingLineups(getUpcomingLineups(player.playerName()))
                .subscription(getSubscriptionInfo(player.playerId()))
                .primaryLeague(player.primaryLeague())
                .build();
    }



    private LastResultDto getLastResult(UUID playerId) {
        return tournamentResultRepository.getLastResult(playerId)
                .map(lastResultProjection ->
                        LastResultDto.builder()
                                .date(lastResultProjection.getDate())
                                .amount(lastResultProjection.getAmount())
                                .resultId(lastResultProjection.getResultId()).build()).orElse(null);
    }


    private List<UpcomingLineupDto> getUpcomingLineups(String playerName) {
        String playerNameLower = playerName.toLowerCase();
        LocalDate today = LocalDate.now();
        List<Lineup> lineups = lineupRepository
                .findByDateBetweenOrderByDateAscTimeAsc(today, today.plusDays(2));

        Map<LocalDate, List<Lineup>> byDate = groupLineupsByDate(lineups);

        LocalDate soonestDate = findSoonestDate(byDate);

        List<UpcomingLineupDto> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Lineup>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Lineup> myLineups = filterMyLineups(entry.getValue(), playerNameLower);

            if (myLineups.isEmpty()) {
                result.add(buildNotInLineupDto(date, soonestDate));
            } else {
                myLineups.forEach(lineup ->
                        result.add(buildInLineupDto(lineup, date, soonestDate)));
            }
        }
        return result;
    }

    private Map<LocalDate, List<Lineup>> groupLineupsByDate(List<Lineup> lineups) {
        return lineups.stream()
                .collect(Collectors.groupingBy(Lineup::getDate, LinkedHashMap::new, Collectors.toList()));
    }

    private LocalDate findSoonestDate(Map<LocalDate, List<Lineup>> byDate) {
        return byDate.keySet().stream().min(LocalDate::compareTo).orElse(null);
    }

    private List<Lineup> filterMyLineups(List<Lineup> lineups, String playerNameLower) {
        return lineups.stream()
                .filter(l -> l.getPlayers().toLowerCase().contains(playerNameLower))
                .toList();
    }

    private UpcomingLineupDto buildNotInLineupDto(LocalDate date, LocalDate soonestDate) {
        return UpcomingLineupDto.builder()
                .date(date.toString())
                .inLineup(false)
                .isSoon(date.equals(soonestDate))
                .build();
    }

    private UpcomingLineupDto buildInLineupDto(Lineup lineup, LocalDate date, LocalDate soonestDate) {
        return UpcomingLineupDto.builder()
                .date(lineup.getDate().toString())
                .time(lineup.getTime())
                .league(lineup.getLeague())
                .inLineup(true)
                .players(StringUtils.capitalize(lineup.getPlayers()))
                .isSoon(date.equals(soonestDate))
                .build();
    }

    private SubscriptionInfoDto getSubscriptionInfo(UUID playerId) {
       return playerClient.getSubscriptionInfo(playerId);
    }
}