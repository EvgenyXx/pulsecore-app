package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.shared.service.CacheEvictionService;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentResultPersistence {

    private final TournamentResultRepository tournamentResultRepository;
    private final CacheEvictionService cacheEvictionService;

    public Page<TournamentResultEntity> getResultsByPeriod(Player player, LocalDate start, LocalDate end, Pageable pageable) {
        return tournamentResultRepository.findByPlayerAndDateBetweenOrderByDateAsc(player, start, end, pageable);
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
            log.error("SAVE ERROR: player={}, tournament={}",
                    entity.getPlayer().getName(), entity.getTournament().getExternalId(), e);
            return entity;
        }
    }

    public PeriodStatsProjection getStatsByPeriod(Player player, LocalDate start, LocalDate end) {
        return tournamentResultRepository.getStats(player, start, end);
    }

    public void evictCaches() {
        cacheEvictionService.evictHallOfFame();
        cacheEvictionService.evictAnalytics();
    }

    private boolean existsByPlayerAndTournament(TournamentResultEntity entity) {
        return tournamentResultRepository.existsByPlayerAndTournament_ExternalId(
                entity.getPlayer(), entity.getTournament().getExternalId());
    }

    private TournamentResultEntity findExisting(TournamentResultEntity entity) {
        return tournamentResultRepository
                .findByPlayerAndTournament_ExternalId(entity.getPlayer(), entity.getTournament().getExternalId())
                .orElse(entity);
    }
}