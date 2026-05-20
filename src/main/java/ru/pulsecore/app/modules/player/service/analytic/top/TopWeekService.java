package ru.pulsecore.app.modules.player.service.analytic.top;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.core.dto.TopPlayerProjection;
import ru.pulsecore.app.modules.player.api.dto.TopWeekResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopWeekService {

    private final PlayerService playerService;
    private final TournamentResultService tournamentResultService;
    private final TournamentResultRepository tournamentResultRepository;

    private LocalDate getWeekStart() {
        return LocalDate.now().with(DayOfWeek.MONDAY);
    }

    private LocalDate getWeekEnd() {
        return LocalDate.now().with(DayOfWeek.SUNDAY);
    }


    public List<TopPlayerProjection> getTopPlayers() {
        return tournamentResultRepository.findTopPlayers(getWeekStart(), 5);
    }

    public TopWeekResponse getTopWithPosition(UUID playerId) {
        Player player = playerService.getById(playerId);
        List<TopPlayerProjection> top5 = getTopPlayers();

        LocalDate start = getWeekStart();
        LocalDate end = getWeekEnd();
        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, start, end);

        long position;
        double playerSum = stats != null ? stats.getSum() : 0;
        if (playerSum > 0) {
            List<Object[]> allStats = tournamentResultRepository.getAllPlayerStats(start, end);
            position = allStats.stream()
                    .filter(r -> ((Number) r[1]).doubleValue() > playerSum)
                    .count() + 1;
        } else {
            position = tournamentResultRepository.countPlayersWithEarnings(start, end) + 1;
        }

        String title = null;
        boolean hasCrown = false;
        if (position == 1) { title = "Рокфеллер"; hasCrown = true; }
        else if (position == 2) title = "Магнат";
        else if (position == 3) title = "Толстосум";

        return TopWeekResponse.builder()
                .playerName(capitalize(player.getName()))
                .playerPosition((int) position)
                .playerTotal(playerSum)
                .playerTournaments(stats != null ? stats.getCount() : 0)
                .title(title)
                .hasCrown(hasCrown)
                .top5(top5.stream().map(p -> TopWeekResponse.TopPlayer.builder()
                        .name(capitalize(p.getName())).total(p.getTotal()).tournaments(p.getTournaments()).build()).toList())
                .build();
    }

    public TopWeekResponse getTopWithPositionByLeague(UUID playerId, String league) {
        Player player = playerService.getById(playerId);
        LocalDate start = getWeekStart();
        LocalDate end = getWeekEnd();

        String primary = getPrimaryLeague(playerId);

        List<TopPlayerProjection> top5 = tournamentResultRepository
                .findTopByPrimaryLeague(start, league, 5);

        if (!league.equals(primary)) {
            return TopWeekResponse.builder()
                    .playerName(capitalize(player.getName()))
                    .playerPosition(0).playerTotal(0).playerTournaments(0)
                    .title(null).hasCrown(false)
                    .top5(top5.stream().map(p -> TopWeekResponse.TopPlayer.builder()
                            .name(capitalize(p.getName())).total(p.getTotal()).tournaments(p.getTournaments()).build()).toList())
                    .build();
        }

        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, start, end);
        double playerSum = stats != null ? stats.getSum() : 0;
        long playerCount = stats != null ? stats.getCount() : 0;
        long position = playerSum > 0 ? top5.stream().filter(p -> p.getTotal() > playerSum).count() + 1 : top5.size() + 1;

        return TopWeekResponse.builder()
                .playerName(capitalize(player.getName()))
                .playerPosition((int) position)
                .playerTotal(playerSum)
                .playerTournaments(playerCount)
                .title(null).hasCrown(false)
                .top5(top5.stream().map(p -> TopWeekResponse.TopPlayer.builder()
                        .name(capitalize(p.getName())).total(p.getTotal()).tournaments(p.getTournaments()).build()).toList())
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