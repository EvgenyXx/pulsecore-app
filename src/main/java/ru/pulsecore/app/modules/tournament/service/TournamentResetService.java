// ==================== TournamentResetService.java ====================
package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.repository.PlayerRepository;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentResetService {

    private final TournamentResultRepository tournamentResultRepository;
    private final PlayerRepository playerRepository;
    private final TournamentAutoAddService tournamentAutoAddService;
    private final TournamentCascadeSyncService cascadeSyncService;

    @Transactional
    public int deleteAllTournaments(UUID playerId) {
        return tournamentResultRepository.deleteByPlayerId(playerId);
    }

    public void resyncAll(UUID playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow();
        tournamentAutoAddService.addRecentTournamentsForPlayer(player, 30);
        cascadeSyncService.syncAllHistory(player);
    }
}