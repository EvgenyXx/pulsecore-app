package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentCancellationService {

    private final TournamentCanceledNotificationService notificationService;
    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository notificationRepository;

    @Transactional
    public void handleCancelled(TournamentEntity t, List<PlayerNotification> notifications) {
        if (t.isCancelled()) return;

        t.setStarted(true);
        t.setCancelled(true);
        t.setProcessed(true);
        t.setFinished(true);
        tournamentRepository.save(t);

        notificationService.sendCancelled(notifications);
        notificationRepository.saveAll(notifications);

        log.info("❌ Tournament cancelled: id={}, players={}", t.getExternalId(), notifications.size());
    }

}

