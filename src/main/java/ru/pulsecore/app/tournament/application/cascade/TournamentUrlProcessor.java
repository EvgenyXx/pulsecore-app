package ru.pulsecore.app.tournament.application.cascade;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.application.match.TournamentMatchService;
import ru.pulsecore.app.tournament.application.resolution.ResultService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultPersistence;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class TournamentUrlProcessor {

    private final ResultService resultService;
    private final TournamentSingleProcessor singleProcessor;
    private final TournamentMatchService matchService;
    private final TournamentResultPersistence resultPersistence;
    private final Bucket tournamentRateLimiter;

    public TournamentUrlProcessor(ResultService resultService,
                                  TournamentSingleProcessor singleProcessor,
                                  TournamentMatchService matchService,
                                  TournamentResultPersistence resultPersistence,
                                  @Qualifier(RateLimiterConfig.HTML_PARSE_RATE_LIMITER) Bucket tournamentRateLimiter) {
        this.resultService = resultService;
        this.singleProcessor = singleProcessor;
        this.matchService = matchService;
        this.resultPersistence = resultPersistence;
        this.tournamentRateLimiter = tournamentRateLimiter;
    }


    @Transactional
    public void processUrlsForPlayer(List<String> urls, UUID playerId, String playerName) {
        List<TournamentEntity> allTournaments = new ArrayList<>();
        List<TournamentMatchEntity> allMatches = new ArrayList<>();
        List<TournamentResultEntity> allResults = new ArrayList<>();

        for (String url : urls) {
            try {
                tournamentRateLimiter.asBlocking().consume(1);
                ParsedResult parsed = resultService.calculateAll(url);

                ProcessedTournament processed = singleProcessor.processSingleUrl(parsed, url, playerId, playerName);

                allTournaments.add(processed.tournament());
                allMatches.addAll(processed.matches());

                processed.result().ifPresent(allResults::add);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Поток прерван. Пропуск: {}", url);
                break;
            }
        }

        if (allTournaments.isEmpty()) {
            log.info("Нет турниров для сохранения");
            return;
        }
        List<Long> tournamentIds = allTournaments.stream().map(TournamentEntity::getId).toList();
        matchService.saveAllMatches(allMatches, tournamentIds);

        resultPersistence.saveResults(allResults);

        log.info("БАТЧ: Сохранение {} турниров, {} матчей, {} результатов",
                allTournaments.size(), allMatches.size(), allResults.size());
    }
}