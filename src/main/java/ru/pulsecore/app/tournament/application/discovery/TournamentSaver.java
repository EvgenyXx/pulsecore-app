package ru.pulsecore.app.tournament.application.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.notification.infrastructure.factory.NotificationFactory;
import ru.pulsecore.app.notification.infrastructure.factory.TournamentFactory;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentSaver {

    private final TournamentRepository tournamentRepository;
    private final NotificationFactory notificationFactory;
    private final PlayerNotificationRepository notificationRepo;
    private final TournamentFactory tournamentFactory;

    public void save(UUID playerId, List<TournamentDto> tournaments) {

        for (TournamentDto t : tournaments) {

            TournamentEntity tournament = tournamentRepository
                    .findByExternalId(t.getId())
                    .orElseGet(() ->
                            tournamentRepository.save(
                                    tournamentFactory.create(t)
                            )
                    );

            PlayerNotification pn = notificationFactory.create(playerId, tournament, t);

            notificationRepo.save(pn);
        }
    }
}