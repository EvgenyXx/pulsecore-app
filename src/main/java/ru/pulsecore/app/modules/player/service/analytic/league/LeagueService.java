// ==================== 2. LeagueService.java ====================
package ru.pulsecore.app.modules.player.service.analytic.league;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueService {

    private final PlayerService playerService;
    private final TournamentResultRepository tournamentResultRepository;

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
}