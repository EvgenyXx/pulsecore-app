package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.notification.infrastructure.factory.NotificationFactory;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationBuilder {

    private final NotificationFactory notificationFactory;
    private final PlayerNotificationRepository notificationRepo;

    public void buildNotificationIfNeeded(
            PlayerData player,
            TournamentEntity tournament,
            TournamentDto t,
            List<PlayerNotification> allNotifications) {

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