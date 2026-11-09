package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PeriodStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.cache.CacheEvictionService;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentResultPersistence {

    private final TournamentResultRepository tournamentResultRepository;
    private final CacheEvictionService cacheEvictionService;


    public Page<TournamentResultEntity> getResultsByPeriod(
            UUID playerId, LocalDate start, LocalDate end, Pageable pageable) {
        return tournamentResultRepository.findByPlayerIdAndDateBetweenOrderByDateAsc(playerId, start, end, pageable);
    }

    @Transactional
    public void saveResults(List<TournamentResultEntity> entities) {
        log.debug("Результаты: сохранение {} записей", entities.size());

        List<TournamentResultEntity> toSave = new ArrayList<>();
        for (TournamentResultEntity entity : entities) {
            if (!tournamentResultRepository.existsByPlayerIdAndTournament_ExternalId(
                    entity.getPlayerId(), entity.getTournament().getExternalId())) {
                toSave.add(entity);
            }
        }
        tournamentResultRepository.saveAll(toSave);
        cacheEvictionService.evictHallOfFame();
        cacheEvictionService.evictAnalytics();
        log.info("Результаты: сохранено={}", toSave.size());

    }


    public PeriodStatsProjection getStatsByPeriod(UUID player, LocalDate start, LocalDate end) {
        return tournamentResultRepository.getStats(player, start, end);
    }




}