
package ru.pulsecore.app.tournament.application.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.application.cascade.TournamentCascadeSyncService;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import java.util.UUID;

/**
 * Сервис для управления данными игроков из админки.
 *
 * deleteAllTournaments — удаляет все результаты игрока.
 * resyncAll — запускает полную пересинхронизацию истории турниров с 2025 года.
 *
 * Используется в AdminPlayerController.
 */
@Service
@RequiredArgsConstructor
public class AdminTournamentManagementService {

    private final TournamentResultRepository tournamentResultRepository;
    private final PlayerClient playerClient;
    private final TournamentCascadeSyncService cascadeSyncService;
    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository  playerNotificationRepository;


    @Transactional
    public int deleteAllTournaments(UUID playerId) {
        int deletedResults = tournamentResultRepository.deleteByPlayerId(playerId);
        int deletedNotifications = playerNotificationRepository.deleteByPlayerId(playerId);
        int deletedTournaments = tournamentRepository.deleteOrphans();
        return deletedResults + deletedNotifications + deletedTournaments;
    }

    @Transactional
    public void resyncAll(UUID playerId) {
        var player = playerClient.getPlayerById(playerId);
        cascadeSyncService.syncAllHistory(player.playerId(), player.playerName());
    }
}