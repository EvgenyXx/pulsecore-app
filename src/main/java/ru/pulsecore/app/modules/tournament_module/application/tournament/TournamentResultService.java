package ru.pulsecore.app.modules.tournament_module.application.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.core.dto.ResultDto;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.TournamentResultEntity;

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

    @Transactional
    public void updateResult(Long id, Double amount, Double bonus) {
        processor.updateResult(id, amount, bonus);
    }

    public TournamentResultEntity save(TournamentResultEntity entity) {
        return persistence.save(entity);
    }

    public PeriodStatsProjection getStatsByPeriod(UUID playerId, LocalDate start, LocalDate end) {
        return persistence.getStatsByPeriod(playerId, start, end);
    }

    public void processResults(List<ResultDto> results, UUID playerId, String playerName, TournamentEntity tournament,
                               double bonus, boolean isFinished, boolean hasRemoved, String league) {
        processor.processResults(results, playerId, playerName, tournament, bonus, isFinished, hasRemoved, league);
    }

    public boolean processResults(List<ResultDto> results, UUID playerId, String playerName, Long tournamentId,
                                  double bonus, boolean isFinished, boolean hasRemoved, String league) {
        return processor.processResults(results, playerId, playerName, tournamentId, bonus, isFinished, hasRemoved, league);
    }
}