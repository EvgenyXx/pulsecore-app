package ru.pulsecore.app.tournament.application.earnings.sum;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultPersistence;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PeriodStatsProjection;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentResultQueryService {

    private final TournamentResultPersistence persistence;


    public Page<TournamentResultEntity> getResultsByPeriod(UUID playerId, LocalDate start, LocalDate end, Pageable pageable) {
        return persistence.getResultsByPeriod(playerId, start, end, pageable);
    }

    public PeriodStatsProjection getStatsByPeriod(UUID playerId, LocalDate start, LocalDate end) {
        return persistence.getStatsByPeriod(playerId, start, end);
    }







}