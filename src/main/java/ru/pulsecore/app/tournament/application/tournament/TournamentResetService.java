
package ru.pulsecore.app.tournament.application.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentResetService {

    private final TournamentResultRepository tournamentResultRepository;
    private final PlayerClient playerClient;
    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;

    @Transactional
    public int deleteAllTournaments(UUID playerId) {
        return tournamentResultRepository.deleteByPlayerId(playerId);
    }

    @Transactional
    public void resyncAll(UUID playerId) {
        var player = playerClient.getPlayerById(playerId);
        tournamentAutoAddService.addRecentTournamentsForPlayer(player.playerId(),player.playerName(),  30);
        cascadeSyncService.syncAllHistory(player.playerId(), player.playerName());
    }
}