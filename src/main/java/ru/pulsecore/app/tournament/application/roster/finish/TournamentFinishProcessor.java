package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishProcessor {

    private final PlayerNotificationRepository repo;
    private final TournamentAsyncFinishProcessor asyncProcessor;


    @Transactional
    public void processFinish() {
        List<String> links = repo.findStartedNotFinishedLinks();
        if (links.isEmpty()) return;
        asyncProcessor.clearStats();
        log.info("Наблюдаются {} турниров", links.size());

        List<CompletableFuture<Void>> futures = links.stream()
                .map(asyncProcessor::processAsync)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        asyncProcessor.logSummary();
    }
}