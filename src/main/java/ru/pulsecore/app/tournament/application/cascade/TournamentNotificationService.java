package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentNotificationService {

    private final PlayerNotificationRepository playerNotificationRepository;

    public void createIfAbsent(UUID playerId, TournamentEntity tournament) {
        boolean exists = playerNotificationRepository
                .findByPlayerIdAndTournamentId(playerId, tournament.getId())
                .isPresent();
        if (!exists) {
            PlayerNotification notification = PlayerNotification.builder()
                    .playerId(playerId)
                    .tournament(tournament)
                    .build();
            playerNotificationRepository.save(notification);
        }
    }
}