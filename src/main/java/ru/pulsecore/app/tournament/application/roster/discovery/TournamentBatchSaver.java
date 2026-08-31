package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentBatchSaver {

    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository notificationRepo;

    public void saveAll(
            List<TournamentEntity> newTournaments,
            List<TournamentEntity> updatedTournaments,
            List<PlayerNotification> allNotifications) {

        saveNewTournaments(newTournaments);
        saveUpdatedTournaments(updatedTournaments);
        saveNotifications(allNotifications);
    }

    private void saveNewTournaments(List<TournamentEntity> newTournaments) {
        if (!newTournaments.isEmpty()) {
            tournamentRepository.saveAll(newTournaments);
            log.info("Сохранение: новых турниров={}, ids={}",
                    newTournaments.size(),
                    newTournaments.stream().map(TournamentEntity::getExternalId).toList());
        }
    }

    private void saveUpdatedTournaments(List<TournamentEntity> updatedTournaments) {
        if (!updatedTournaments.isEmpty()) {
            tournamentRepository.saveAll(updatedTournaments);
            log.info("Обновлены externalId у {} турниров", updatedTournaments.size());
        }
    }

    private void saveNotifications(List<PlayerNotification> allNotifications) {
        notificationRepo.saveAll(allNotifications);
        log.info("Сохранение: уведомлений создано={}", allNotifications.size());
    }
}
