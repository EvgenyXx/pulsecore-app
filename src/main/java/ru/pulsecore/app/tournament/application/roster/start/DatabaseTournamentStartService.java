package ru.pulsecore.app.tournament.application.roster.start;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Profile("start-db")
@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseTournamentStartService implements TournamentStartService {

    private final TournamentRepository tournamentRepository;
    private final TournamentTimeService tournamentTimeService;

    @Transactional
    @Override
    public void checkStart() {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Moscow"));
        List<TournamentEntity> tournaments = tournamentRepository.findNotStartedTournaments(today);

        List<TournamentEntity> started = tournaments.stream()
                .filter(tournamentTimeService::isStartedByTime)
                .peek(t -> t.setStarted(true))
                .toList();

        tournamentRepository.saveAll(started);
        log.debug("Старт DB: запущено {} турниров", started.size());
    }
}
