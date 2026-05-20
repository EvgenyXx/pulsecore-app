package ru.pulsecore.app.modules.lineup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.lineup.client.MastersApiClient;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.mapper.LineupMapper;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.lineup.validator.TournamentValidator;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineupService {

    private final LineupRepository lineupRepository;
    private final MastersApiClient apiClient;
    private final LineupMapper mapper;
    private final TournamentValidator validator;
    private final PlayerService playerService;

    @Transactional
    public void cleanupOld() {
        lineupRepository.deleteByDateBefore(LocalDate.now());
        log.info("Cleaned old lineups");
    }

    @Transactional
    public void loadLineups() {
        List<Player> players = playerService.getAll();
        if (players.isEmpty()) return;

        LocalDate today = LocalDate.now();
        loadDay(players, today);
        loadDay(players, today.plusDays(1));
        loadDay(players, today.plusDays(2));
    }

    private void loadDay(List<Player> players, LocalDate date) {
        List<TournamentDto> all = apiClient.loadTournaments(date.toString());
        if (all.isEmpty()) return;

        Set<String> names = new HashSet<>();
        for (Player p : players) names.add(p.getName().toLowerCase());

        List<TournamentDto> relevant = all.stream()
                .filter(t -> t.getPlayers().stream().anyMatch(n -> names.contains(n.toLowerCase())))
                .filter(validator::isValid)
                .filter(t -> date.equals(extractDate(t)))
                .toList();

        if (relevant.isEmpty()) return;

        if (date.isAfter(LocalDate.now())) {
            lineupRepository.deleteAllByDate(date);
        }

        List<Lineup> lineups = relevant.stream()
                .map(t -> mapper.toEntity(t, date, extractTime(t)))
                .toList();

        for (Lineup lineup : lineups) {
            lineupRepository.upsertLineup(
                    lineup.getDate(),
                    lineup.getLeague(),
                    lineup.getTime(),
                    lineup.getPlayers()
            );
        }

        log.info("{} lineups for date {}", lineups.size(), date);
    }

    private LocalDate extractDate(TournamentDto t) {
        try {
            return LocalDate.parse(t.getDate().getDate().substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    private String extractTime(TournamentDto t) {
        try {
            return t.getDate().getDate().substring(11, 16);
        } catch (Exception e) {
            return "??:??";
        }
    }
}