package ru.pulsecore.app.tournament.application.roster.broken;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrokenTournamentCleanupService {

    private final TournamentRepository tournamentRepository;

    @Transactional
    public void cleanupBrokenTournament() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);

        List<TournamentEntity> broken = tournamentRepository.findByBrokenTrueAndDateBetween(start, end);
        if (broken.isEmpty()) {
            log.debug("BROKEN CLEANUP: нет турниров для удаления за {}..{}", start, end);
            return;
        }
        tournamentRepository.deleteAll(broken);
        log.info("BROKEN CLEANUP: удалено турниров={} за {}-{}", broken.size(), start, end);
    }
}
