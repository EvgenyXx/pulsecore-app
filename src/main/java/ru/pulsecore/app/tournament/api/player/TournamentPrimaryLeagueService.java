package ru.pulsecore.app.tournament.api.player;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;
import ru.pulsecore.app.tournament.application.mapping.TournamentMapper;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentResultRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentPrimaryLeagueService {

    private final TournamentResultRepository tournamentResultRepository;
    private final TournamentMapper tournamentMapper;

    public List<PriorityLeagueResponse> getLeagues(Set<UUID> playerIds) {
        log.debug("Запрос лиг для игроков: count={}", playerIds.size());

        List<PriorityLeagueResponse> result = tournamentResultRepository.findPrimaryLeagues(playerIds)
                .stream()
                .map(tournamentMapper::toPriorityLeague)
                .toList();

        log.debug("Лиги получены: count={}", result.size());
        return result;
    }
}
