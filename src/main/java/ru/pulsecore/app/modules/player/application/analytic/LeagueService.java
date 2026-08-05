
package ru.pulsecore.app.modules.player.application.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.application.player.PlayerService;
import ru.pulsecore.app.modules.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeagueService {

    private final PlayerService playerService;
    private final TournamentResultRepository tournamentResultRepository;

    public String getPrimaryLeague(UUID playerId) {//todo перенести в туринры
        List<String> lastLeagues = tournamentResultRepository.findLastLeagues(playerId);
        if (lastLeagues.isEmpty()) return "A";
        return lastLeagues.stream()
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(lastLeagues.get(0));
    }
}