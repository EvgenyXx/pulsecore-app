package ru.pulsecore.app.tournament.application.roster.change.info;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.cache.DiscoveryHashCache;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.DiscoveryHashUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentHashChecker {

    private final DiscoveryHashCache hashCache;
    private final TournamentChangeAnalyzer analyzer;

    public void checkAndUpdateHashes(
            Map<String, List<TournamentDto>> playerTournaments,
            Map<String, List<TournamentDto>> allTournaments) {

        log.debug("Хэши: начало проверки, игроков={}", playerTournaments.size());

        Set<Long> processed = new HashSet<>();
        int updated = 0;
        int unchanged = 0;
        int deleted = 0;

        for (List<TournamentDto> tournaments : playerTournaments.values()) {
            for (TournamentDto tournament : tournaments) {
                if (!processed.add(tournament.getId())) continue;

                String date = extractDate(tournament);
                String hash = DiscoveryHashUtil.calculateTournamentHash(tournament);

                if (!isFuture(tournament)) {
                    hashCache.delete(date, tournament.getId());
                    log.debug("Хэши: удалён устаревший турнир={}", tournament.getId());
                    deleted++;
                    continue;
                }

                if (hasHashChanged(date, tournament, hash)) {
                    log.debug("Хэши: изменения в турнире={}, link={}", tournament.getId(), tournament.getLink());
                    analyzeAndUpdate(date, tournament, hash, allTournaments);
                    updated++;
                } else {
                    unchanged++;
                }
            }
        }

        log.info("Хэши турниров: обновлено={}, без изменений={}, удалено={}",
                updated, unchanged, deleted);
    }

    private String extractDate(TournamentDto tournament) {
        return tournament.getDate().getDate().split(" ")[0];
    }

    private boolean hasHashChanged(String date, TournamentDto tournament, String hash) {
        return !hashCache.hasSameHash(date, tournament.getId(), hash);
    }

    private void analyzeAndUpdate(
            String date,
            TournamentDto tournament,
            String hash,
            Map<String, List<TournamentDto>> allTournaments) {

        log.debug("Хэши: анализ турнира={}", tournament.getId());
        analyzer.analyze(tournament, allTournaments);
        hashCache.update(date, tournament.getId(), hash);
    }

    private boolean isFuture(TournamentDto t) {
        return t.getDate() != null && DateTimeUtils.isFuture(t.getDate().getDate());
    }
}