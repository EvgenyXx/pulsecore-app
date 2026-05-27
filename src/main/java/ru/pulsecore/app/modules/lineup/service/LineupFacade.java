package ru.pulsecore.app.modules.lineup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class LineupFacade {

    private final LineupService lineupService;
    private final LineupRepository lineupRepository;
    private final PlayerService playerService;

    public Map<String, List<Lineup>> getAllGroupedByHall(LocalDate date) {
        List<Lineup> all = lineupRepository.findByDate(date);
        return lineupService.groupByHall(all);
    }

    public Map<String, List<Lineup>> getMyGroupedByHall(UUID playerId, LocalDate date) {
        String hallsStr = playerService.getSelectedHalls(playerId);
        if (hallsStr == null || hallsStr.isBlank()) {
            return Map.of();
        }
        List<String> halls = Arrays.asList(hallsStr.split(",\\s*"));
        List<Lineup> filtered = lineupService.getLineupsForHalls(date, halls);
        return lineupService.groupByHall(filtered);
    }
}