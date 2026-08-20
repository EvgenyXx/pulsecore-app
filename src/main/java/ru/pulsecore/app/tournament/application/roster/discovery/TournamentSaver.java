package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.notification.infrastructure.factory.NotificationFactory;
import ru.pulsecore.app.notification.infrastructure.factory.TournamentFactory;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSaver {

    private final TournamentRepository tournamentRepository;
    private final NotificationFactory notificationFactory;
    private final PlayerNotificationRepository notificationRepo;
    private final TournamentFactory tournamentFactory;

    @Transactional
    public void saveAll(Map<PlayerData, List<TournamentDto>> data) {
        List<TournamentEntity> newTournaments = new ArrayList<>();
        List<TournamentEntity> updatedTournaments = new ArrayList<>();
        List<PlayerNotification> allNotifications = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        for (var entry : data.entrySet()) {
            PlayerData player = entry.getKey();
            for (TournamentDto t : entry.getValue()) {
                TournamentEntity tournament = tournamentRepository
                        .findByLink(t.getLink())
                        .map(existing -> {

                            if (!existing.getExternalId().equals(t.getId())) {
                                existing.setExternalId(t.getId());
                                updatedTournaments.add(existing);
                            }
                            return existing;
                        })
                        .orElseGet(() -> {
                            if (seen.add(t.getId())) {
                                TournamentEntity newT = tournamentFactory.create(t);
                                newTournaments.add(newT);
                                return newT;
                            }
                            return newTournaments.stream()
                                    .filter(nt -> nt.getExternalId().equals(t.getId()))
                                    .findFirst()
                                    .orElseThrow();
                        });

                boolean exists = notificationRepo
                        .findByPlayerIdAndTournamentId(player.playerId(), tournament.getId())
                        .isPresent();


                if (!exists) {
                    allNotifications.add(notificationFactory.create(player.playerId(), tournament, t));
                }
            }
        }

        if (!newTournaments.isEmpty()) {
            tournamentRepository.saveAll(newTournaments);
        }

        if (!updatedTournaments.isEmpty()) {
            tournamentRepository.saveAll(updatedTournaments);
            log.info("Обновлены externalId у {} турниров", updatedTournaments.size());
        }

        notificationRepo.saveAll(allNotifications);
    }
}