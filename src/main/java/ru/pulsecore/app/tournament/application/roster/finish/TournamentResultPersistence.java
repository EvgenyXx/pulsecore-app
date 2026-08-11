package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PeriodStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.cache.CacheEvictionService;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

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

    public void saveRoster(List<TournamentResultEntity> entities) {
        List<TournamentResultEntity> toSave = new ArrayList<>();
        for (TournamentResultEntity entity : entities) {
            if (!existsByPlayerAndTournament(entity)) {
                toSave.add(entity);
            }
        }
        if (!toSave.isEmpty()) {
            tournamentResultRepository.saveAll(toSave);
            evictCaches();
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


}