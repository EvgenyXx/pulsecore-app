package ru.pulsecore.app.tournament.application.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.application.cascade.TournamentCascadeSyncService;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Сервис для управления данными игроков из админки.
 *
 * deleteAllTournaments — удаляет все результаты игрока.
 * resyncPeriod — запускает пересинхронизацию турниров за период.
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
    private final PlayerNotificationRepository playerNotificationRepository;

    @Transactional
    public int deleteAllTournaments(UUID playerId) {
        int deletedResults = tournamentResultRepository.deleteByPlayerId(playerId);
        int deletedNotifications = playerNotificationRepository.deleteByPlayerId(playerId);
        int deletedTournaments = tournamentRepository.deleteOrphans();
        return deletedResults + deletedNotifications + deletedTournaments;
    }

    /**
     * Пересинхронизация за указанный период.
     * Даты приходят из админки.
     */
    @Transactional
    public void resyncPeriod(UUID playerId, LocalDate from, LocalDate to) {
        var player = playerClient.getPlayerById(playerId);
        cascadeSyncService.syncPeriod(player.playerId(), player.playerName(), from, to);
    }
}