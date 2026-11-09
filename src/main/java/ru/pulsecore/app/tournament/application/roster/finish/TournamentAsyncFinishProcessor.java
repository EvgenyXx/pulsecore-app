package ru.pulsecore.app.tournament.application.roster.finish;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.cache.TournamentStatusCache;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentParser;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentStatusParser;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class TournamentAsyncFinishProcessor {

    private final DocumentLoader documentLoader;
    private final TournamentFinishService finishService;
    private final JsonTournamentParser jsonTournamentParser;
    private final JsonTournamentStatusParser jsonTournamentStatusParser;
    private final TournamentStatusCache tournamentStatusCache;
    private final Bucket finishRateLimiter;

    private final Map<String, Integer> stats = new ConcurrentHashMap<>();

    public TournamentAsyncFinishProcessor(DocumentLoader documentLoader,
                                          TournamentFinishService finishService,
                                          JsonTournamentParser jsonTournamentParser,
                                          JsonTournamentStatusParser jsonTournamentStatusParser,
                                          TournamentStatusCache tournamentStatusCache,
                                          @Qualifier(RateLimiterConfig.FINISH_RATE_LIMITER) Bucket finishRateLimiter) {
        this.documentLoader = documentLoader;
        this.finishService = finishService;
        this.jsonTournamentParser = jsonTournamentParser;
        this.jsonTournamentStatusParser = jsonTournamentStatusParser;
        this.tournamentStatusCache = tournamentStatusCache;
        this.finishRateLimiter = finishRateLimiter;
    }

    @Async(AsyncConfig.TOURNAMENT_EXECUTOR)
    public CompletableFuture<Void> processAsync(String link) {
        finishRateLimiter.asBlocking().consumeUninterruptibly(1);
        process(link);
        return CompletableFuture.completedFuture(null);
    }

    private void process(String link) {

        if (tournamentStatusCache.isInProgress(link)) {
            stats.merge("В КЭШЕ", 1, Integer::sum);
            return;
        }

        Document doc = documentLoader.load(link);

        TournamentPage page = jsonTournamentParser.parse(doc);


        TournamentStatus status = jsonTournamentStatusParser.parseStatus(page);

        if (status == TournamentStatus.IN_PROGRESS) {
            tournamentStatusCache.setInProgress(link);
            stats.merge("IN_PROGRESS", 1, Integer::sum);
            return;
        }

        tournamentStatusCache.remove(link);
        stats.merge("ЗАВЕРШЁН", 1, Integer::sum);

        try {
            finishService.handleFinished(link, page);
        } catch (TournamentNotFoundException e) {
            log.warn(e.getMessage());
        }
    }

    public void logSummary() {
        int inCache = stats.getOrDefault("В КЭШЕ", 0);
        int inProgress = stats.getOrDefault("IN_PROGRESS", 0);
        int finished = stats.getOrDefault("ЗАВЕРШЁН", 0);

        int going = inCache + inProgress;

        String icon;
        if (going > 0) {
            icon = "⏳";
        } else if (finished > 0) {
            icon = "✅";
        } else {
            icon = "❔";
        }

        log.info("{} FINISH: итог — в кэше={}, in_progress={}, завершено={}",
                icon, inCache, inProgress, finished);
    }

    public void clearStats() {
        stats.clear();
    }
}