package ru.pulsecore.app.modules.tournament_module.application.tournament;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.shared.service.cache.CacheEvictionService;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.TournamentResultEntity;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
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

    public TournamentResultEntity save(TournamentResultEntity entity) {
        if (existsByPlayerAndTournament(entity)) {
            return findExisting(entity);
        }

        try {
            TournamentResultEntity saved = tournamentResultRepository.save(entity);
            evictCaches();
            return saved;
        } catch (Exception e) {

            return entity;
        }
    }

    public PeriodStatsProjection getStatsByPeriod(UUID player, LocalDate start, LocalDate end) {
        return tournamentResultRepository.getStats(player, start, end);
    }

    public void evictCaches() {
        cacheEvictionService.evictHallOfFame();
        cacheEvictionService.evictAnalytics();
    }

    private boolean existsByPlayerAndTournament(TournamentResultEntity entity) {
        return tournamentResultRepository.existsByPlayerIdAndTournament_ExternalId(
                entity.getPlayerId(), entity.getTournament().getExternalId());
    }

    private TournamentResultEntity findExisting(TournamentResultEntity entity) {
        return tournamentResultRepository
                .findByPlayerIdAndTournament_ExternalId(entity.getPlayerId(), entity.getTournament().getExternalId())
                .orElse(entity);
    }
}