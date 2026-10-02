package ru.pulsecore.app.tournament.infrastructure.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.client.TournamentClient;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;
import ru.pulsecore.app.tournament.api.player.TournamentPrimaryLeagueService;
import ru.pulsecore.app.tournament.infrastructure.repository.ChatMessageRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerTournamentClientImp implements TournamentClient {

    private final ChatMessageRepository chatMessageRepository;
    private final TournamentPrimaryLeagueService tournamentPrimaryLeagueService;


    @Override
    public void deleteByPlayerId(UUID playerId) {
        chatMessageRepository.deleteByPlayerId(playerId);
    }

    @Override
    public List<PriorityLeagueResponse> getLeagues(Set<UUID> playerIds) {
        return tournamentPrimaryLeagueService.getLeagues(playerIds);
    }

}
