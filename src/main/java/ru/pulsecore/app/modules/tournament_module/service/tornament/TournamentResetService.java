
package ru.pulsecore.app.modules.tournament_module.service.tornament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.tournament_module.client.PlayerClient;
import ru.pulsecore.app.modules.tournament_module.repo.TournamentResultRepository;

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

    public void resyncAll(UUID playerId) {
        var player = playerClient.getPlayerById(playerId);
        tournamentAutoAddService.addRecentTournamentsForPlayer(player.playerId(),player.playerName(),  30);
        cascadeSyncService.syncAllHistory(player.playerId(), player.playerName());
    }
}