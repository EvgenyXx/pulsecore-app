package ru.pulsecore.app.modules.player.service;

import org.springframework.cache.annotation.Cacheable;
import ru.pulsecore.app.core.dto.*;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.player.api.dto.*;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// TODO: Разбить на более мелкие сервисы:
//       - PlayerLeagueService (getPrimaryLeague, getTopWithPositionByLeague)
//       - PlayerDashboardService (getDashboard)
//       - PlayerSumService (getSum)
//       - TopWeekService (getTopWithPosition, getTopPlayers)

@Service
@RequiredArgsConstructor
public class PlayerStatsService {

    private final PlayerService playerService;
    private final TournamentResultService tournamentResultService;
    private final TournamentResultRepository tournamentResultRepository;
    private final LineupRepository lineupRepository;

    public TopWeekResponse getTopWithPosition(UUID playerId) {
        Player player = playerService.getById(playerId);
        List<TopPlayerProjection> top5 = getTopPlayers();

        LocalDate weekAgo = LocalDate.now().minusDays(6);
        LocalDate today = LocalDate.now();
        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, weekAgo, today);

        long position;
        double playerSum = stats != null ? stats.getSum() : 0;
        if (playerSum > 0) {
            List<Object[]> allStats = tournamentResultRepository.getAllPlayerStats(weekAgo, today);
            position = allStats.stream()
                    .filter(r -> ((Number) r[1]).doubleValue() > playerSum)
                    .count() + 1;
        } else {
            position = tournamentResultRepository.countPlayersWithEarnings(weekAgo, today) + 1;
        }

        String title = null;
        boolean hasCrown = false;
        if (position == 1) { title = "Рокфеллер"; hasCrown = true; }
        else if (position == 2) title = "Магнат";
        else if (position == 3) title = "Толстосум";

        return TopWeekResponse.builder()
                .playerName(player.getName())
                .playerPosition((int) position)
                .playerTotal(playerSum)
                .playerTournaments(stats != null ? stats.getCount() : 0)
                .title(title)
                .hasCrown(hasCrown)
                .top5(top5.stream().map(p -> TopWeekResponse.TopPlayer.builder()
                        .name(p.getName()).total(p.getTotal()).tournaments(p.getTournaments()).build()).toList())
                .build();
    }

    public TopWeekResponse getTopWithPositionByLeague(UUID playerId, String league) {
        Player player = playerService.getById(playerId);
        LocalDate weekAgo = LocalDate.now().minusDays(6);
        LocalDate today = LocalDate.now();

        String primary = getPrimaryLeague(playerId);

        List<TopPlayerProjection> top5 = tournamentResultRepository
                .findTopByPrimaryLeague(weekAgo, league, 5);



        if (!league.equals(primary)) {
            return TopWeekResponse.builder()
                    .playerName(player.getName())
                    .playerPosition(0).playerTotal(0).playerTournaments(0)
                    .title(null).hasCrown(false)
                    .top5(top5.stream().map(p -> TopWeekResponse.TopPlayer.builder()
                            .name(p.getName()).total(p.getTotal()).tournaments(p.getTournaments()).build()).toList())
                    .build();
        }

        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, weekAgo, today);
        double playerSum = stats != null ? stats.getSum() : 0;
        long playerCount = stats != null ? stats.getCount() : 0;
        long position = playerSum > 0 ? top5.stream().filter(p -> p.getTotal() > playerSum).count() + 1 : top5.size() + 1;

        String title = null;
        boolean hasCrown = false;

        return TopWeekResponse.builder()
                .playerName(player.getName())
                .playerPosition((int) position)
                .playerTotal(playerSum)
                .playerTournaments(playerCount)
                .title(title).hasCrown(hasCrown)
                .top5(top5.stream().map(p -> TopWeekResponse.TopPlayer.builder()
                        .name(p.getName()).total(p.getTotal()).tournaments(p.getTournaments()).build()).toList())
                .build();
    }

    public String getPrimaryLeague(UUID playerId) {
        Player player = playerService.getById(playerId);
        List<String> lastLeagues = tournamentResultRepository.findLastLeagues(player);
        if (lastLeagues.isEmpty()) return "A";
        return lastLeagues.stream()
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(lastLeagues.get(0));
    }

    @Cacheable(value = "topWeek", key = "'week'")
    public List<TopPlayerProjection> getTopPlayers() {
        return tournamentResultRepository.findTopPlayers(LocalDate.now().minusDays(6), 5);
    }

    public DashboardResponse getDashboard(UUID id) {
        Player player = playerService.getById(id);
        String playerNameLower = player.getName().toLowerCase();
        TopWeekResponse topWeek = getTopWithPosition(id);
        String primaryLeague = getPrimaryLeague(id);

        var lastResult = tournamentResultRepository.findTopByPlayerOrderByDateDesc(player)
                .map(r -> LastResultDto.builder()
                        .date(r.getDate().toString()).amount(r.getAmount())
                        .tournamentLink(r.getTournament().getLink()).build())
                .orElse(null);

        LocalDate today = LocalDate.now();
        List<Lineup> lineups = lineupRepository.findByDateBetweenOrderByDateAscTimeAsc(today.plusDays(1), today.plusDays(2));
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
                            .players(lineup.getPlayers()).isSoon(date.equals(soonestDate)).build());
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
                .playerName(player.getName()).lastResult(lastResult)
                .upcomingLineups(upcomingLineups).subscription(subInfo)
                .topWeekTitle(topWeek.getTitle()).hasCrown(topWeek.isHasCrown())
                .primaryLeague(primaryLeague).build();
    }

    public SumResponse getSum(UUID id, LocalDate start, LocalDate end) {
        Player player = playerService.getById(id);
        if (start == null && end == null)
            return SumResponse.builder().playerName(player.getName()).start("").end("")
                    .sum(0.0).average(0.0).count(0L).tournaments(List.of()).build();
        if (start == null) start = end;
        if (end == null) end = start;
        if (end.toEpochDay() - start.toEpochDay() > 90) end = start.plusDays(90);

        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, start, end);
        var entities = tournamentResultService.getResultsByPeriod(player, start, end);
        if (entities.size() > 50) entities = entities.subList(0, 50);

        List<SumResponse.TournamentItem> tournaments = entities.stream()
                .map(e -> SumResponse.TournamentItem.builder()
                        .date(e.getDate().toString()).amount(e.getAmount())
                        .resultId(e.getId()).hasRemoved(e.isHasRemoved()).build())
                .collect(Collectors.toList());

        return SumResponse.builder()
                .playerName(player.getName()).start(start.toString()).end(end.toString())
                .sum(stats != null ? stats.getSum() : 0)
                .average(stats != null ? stats.getAverage() : 0)
                .count(stats != null ? stats.getCount() : 0)
                .tournaments(tournaments).build();
    }
}