package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentCancellationService {

    private final TournamentCanceledNotificationService notificationService;
    private final TournamentRepository tournamentRepository;

    @Transactional
    public void handleCancelled(String url) {
        tournamentRepository.findByLink(url).ifPresent(tournament -> {
            if (tournament.isCancelled()) return;

            tournament.setStarted(true);
            tournament.setCancelled(true);
            tournament.setProcessed(true);
            tournament.setFinished(true);
            tournamentRepository.save(tournament);

            notificationService.sendCancelled(tournament);
        });
    }
}