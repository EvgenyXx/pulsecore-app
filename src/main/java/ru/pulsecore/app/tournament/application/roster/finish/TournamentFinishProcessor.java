package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;



@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishProcessor {

    private final TournamentAsyncFinishProcessor asyncProcessor;
    private final TournamentRepository tournamentRepository;


    @Transactional
    public void processFinish() {
        List<String>links = tournamentRepository.findStartedNotFinishedLinks();

        asyncProcessor.clearStats();
        log.debug("Наблюдаются {} турниров", links.size());

        List<CompletableFuture<Void>> futures = links.stream()
                .map(asyncProcessor::processAsync)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        asyncProcessor.logSummary();
    }
}