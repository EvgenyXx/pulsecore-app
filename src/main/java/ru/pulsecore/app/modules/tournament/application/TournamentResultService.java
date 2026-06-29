package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.core.dto.ResultDto;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentResultService {

    private final TournamentResultPersistence persistence;
    private final TournamentResultProcessor processor;

    public Page<TournamentResultEntity> getResultsByPeriod(Player player, LocalDate start, LocalDate end, Pageable pageable) {
        return persistence.getResultsByPeriod(player, start, end, pageable);
    }

    @Transactional
    public void updateResult(Long id, Double amount, Double bonus) {
        processor.updateResult(id, amount, bonus);
    }

    public TournamentResultEntity save(TournamentResultEntity entity) {
        return persistence.save(entity);
    }

    public PeriodStatsProjection getStatsByPeriod(Player player, LocalDate start, LocalDate end) {
        return persistence.getStatsByPeriod(player, start, end);
    }

    public void processResults(List<ResultDto> results, Player player, TournamentEntity tournament,
                               double bonus, boolean isFinished, boolean hasRemoved, String league) {
        processor.processResults(results, player, tournament, bonus, isFinished, hasRemoved, league);
    }

    public boolean processResults(List<ResultDto> results, Player player, Long tournamentId,
                                  double bonus, boolean isFinished, boolean hasRemoved, String league) {
        return processor.processResults(results, player, tournamentId, bonus, isFinished, hasRemoved, league);
    }
}