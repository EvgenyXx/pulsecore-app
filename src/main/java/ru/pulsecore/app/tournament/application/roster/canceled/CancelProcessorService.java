package ru.pulsecore.app.tournament.application.roster.canceled;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CancelProcessorService {

    private final TournamentCanceledService tournamentCanceledService;
    private final TournamentRepository tournamentRepository;

    public void processCanceled() {
        List<String> links = tournamentRepository.findNotStartedForCancelLinks();
        log.info("Началась проверка отмененых и битых турниров в количестве:{}", links.size());
        links.forEach(tournamentCanceledService::processLink);
    }
}
