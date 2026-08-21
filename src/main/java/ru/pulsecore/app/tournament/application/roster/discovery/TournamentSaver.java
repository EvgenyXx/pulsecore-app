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
        log.debug("Сохранение: игроков={}, всего турниров={}",
                data.size(),
                data.values().stream().mapToInt(List::size).sum());

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
                            log.debug("Сохранение: найден существующий турнир={}, link={}",
                                    existing.getExternalId(), t.getLink());

                            if (!existing.getExternalId().equals(t.getId())) {
                                log.debug("Сохранение: externalId изменился {} -> {}",
                                        existing.getExternalId(), t.getId());
                                existing.setExternalId(t.getId());
                                updatedTournaments.add(existing);
                            }
                            return existing;
                        })
                        .orElseGet(() -> {
                            if (seen.add(t.getId())) {
                                log.debug("Сохранение: создание нового турнира={}", t.getId());
                                TournamentEntity newT = tournamentFactory.create(t);
                                newTournaments.add(newT);
                                return newT;
                            }
                            log.debug("Сохранение: дубликат в текущем батче, id={}", t.getId());
                            return newTournaments.stream()
                                    .filter(nt -> nt.getExternalId().equals(t.getId()))
                                    .findFirst()
                                    .orElseThrow();
                        });

                boolean exists = notificationRepo
                        .findByPlayerIdAndTournamentId(player.playerId(), tournament.getId())
                        .isPresent();

                if (!exists) {
                    log.debug("Сохранение: новая связь player={}, tournament={}",
                            player.playerName(), tournament.getExternalId());
                    allNotifications.add(notificationFactory.create(player.playerId(), tournament, t));
                } else {
                    log.debug("Сохранение: связь уже существует player={}, tournament={}",
                            player.playerName(), tournament.getExternalId());
                }
            }
        }

        if (!newTournaments.isEmpty()) {
            tournamentRepository.saveAll(newTournaments);
            log.info("Сохранение: новых турниров={}, ids={}",
                    newTournaments.size(),
                    newTournaments.stream().map(TournamentEntity::getExternalId).toList());
        }

        if (!updatedTournaments.isEmpty()) {
            tournamentRepository.saveAll(updatedTournaments);
            log.info("Обновлены externalId у {} турниров", updatedTournaments.size());
        }

        notificationRepo.saveAll(allNotifications);
        log.info("Сохранение: уведомлений создано={}", allNotifications.size());
    }
}