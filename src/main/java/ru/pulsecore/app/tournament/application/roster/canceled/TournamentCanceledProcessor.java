package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * Обрабатывает проверку отмены турниров.
 *
 * <p>Берёт из базы турниры, которые ещё не начались, и передаёт их в
 * {@link TournamentCanceledService} для асинхронной проверки на сайте.
 * В конце выводит итоговую статистику.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentCanceledProcessor {

    private final PlayerNotificationRepository notificationRepository;
    private final TournamentCanceledService tournamentCanceledService;


    public void processCanceled() {
        List<String> links = notificationRepository.findNotStartedForCancelLinks();
        if (links.isEmpty()) return;

        tournamentCanceledService.clearStats();
        List<CompletableFuture<Void>> futures = links.stream()
                .map(tournamentCanceledService::processLink)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        tournamentCanceledService.logSummary();
    }


}
