package ru.pulsecore.app.modules.tournament_module.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.tournament_module.entity.PlayerNotification;
import ru.pulsecore.app.modules.notification_modules.infrastructure.factory.NotificationFactory;
import ru.pulsecore.app.modules.notification_modules.infrastructure.factory.TournamentFactory;
import ru.pulsecore.app.modules.tournament_module.repo.PlayerNotificationRepository;
import ru.pulsecore.app.modules.tournament_module.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament_module.repo.TournamentRepository;

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