package ru.pulsecore.app.tournament.application.result;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PeriodStatsProjection;
import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentResultService {

    private final TournamentResultPersistence persistence;
    private final TournamentResultProcessor processor;

    public Page<TournamentResultEntity> getResultsByPeriod(UUID playerId, LocalDate start, LocalDate end, Pageable pageable) {
        return persistence.getResultsByPeriod(playerId, start, end, pageable);
    }



    public TournamentResultEntity save(TournamentResultEntity entity) {
        return persistence.save(entity);
    }

    public PeriodStatsProjection getStatsByPeriod(UUID playerId, LocalDate start, LocalDate end) {
        return persistence.getStatsByPeriod(playerId, start, end);
    }



    public void processResults(List<ResultDto> results, UUID playerId, String playerName, Long tournamentId,
                               double bonus, boolean isFinished, boolean hasRemoved, String league) {
        processor.processResults(results, playerId, playerName, tournamentId, bonus, isFinished, hasRemoved, league);
    }
}