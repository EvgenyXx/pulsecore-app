package ru.pulsecore.app.tournament.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.client.TournamentClient;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.ChatMessageRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerTournamentClientImp implements TournamentClient {

    private final ChatMessageRepository chatMessageRepository;
    private final TournamentResultRepository tournamentResultRepository;


    @Override
    public void deleteByPlayerId(UUID playerId) {
        chatMessageRepository.deleteByPlayerId(playerId);
    }

    @Override
    public List<PriorityLeagueResponse> getLeagues(Set<UUID> playerIds) {
        return tournamentResultRepository.findPrimaryLeagues(playerIds)
                .stream()
                .map(p->
                        new PriorityLeagueResponse(p.getPlayerId(),p.getLeague()))
                .toList();
    }
}
