package ru.pulsecore.app.tournament.application.roster.change.info;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.cache.DiscoveryHashCache;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.hash.TournamentHashCalculator;

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
    private final TournamentHashCalculator hashCalculator;

    public void checkAndUpdateHashes(
            Map<String, List<TournamentDto>> playerTournaments,
            Map<String, List<TournamentDto>> allTournaments) {

        log.debug("Хэши: начало проверки, игроков={}", playerTournaments.size());

        Set<Long> processed = new HashSet<>();


        for (List<TournamentDto> tournaments : playerTournaments.values()) {
            for (TournamentDto tournament : tournaments) {
                if (!processed.add(tournament.getId())) continue;

                String date = DateTimeUtils.formatDate(tournament.getDate().getDate());
                String hash = hashCalculator.calculateTournamentHash(tournament);

                if (!DateTimeUtils.isFuture(tournament)) {
                    hashCache.delete(date, tournament.getId());
                    log.debug("Хэши: удалён устаревший турнир={}", tournament.getId());
                    continue;
                }
                if (!hashCache.hasSameHash(date, tournament.getId(), hash)) {
                    log.debug("Хэши: изменения в турнире={}, link={}", tournament.getId(), tournament.getLink());
                    analyzer.analyze(tournament, allTournaments);
                    hashCache.update(date, tournament.getId(), hash);
                }
            }
        }

    }


}