package ru.pulsecore.app.tournament.application.roster.finish;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.cache.TournamentStatusCache;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentStatusParser;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TournamentAsyncFinishProcessor {

    private final DocumentLoader documentLoader;
    private final TournamentFinishService finishService;
    private final PlayerNotificationRepository repo;
    private final JsonTournamentStatusParser jsonTournamentStatusParser;
    private final TournamentStatusCache tournamentStatusCache;
    private final Bucket finishRateLimiter;

    private final Map<String, Integer> stats = new ConcurrentHashMap<>();

    public TournamentAsyncFinishProcessor(DocumentLoader documentLoader,
                                          TournamentFinishService finishService,
                                          PlayerNotificationRepository repo,
                                          JsonTournamentStatusParser jsonTournamentStatusParser,
                                          TournamentStatusCache tournamentStatusCache,
                                          @Qualifier(RateLimiterConfig.FINISH_RATE_LIMITER) Bucket finishRateLimiter) {
        this.documentLoader = documentLoader;
        this.finishService = finishService;
        this.repo = repo;
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
        TournamentStatus status = jsonTournamentStatusParser.parseStatus(doc);

        if (status == TournamentStatus.IN_PROGRESS) {
            tournamentStatusCache.setInProgress(link);
            stats.merge("IN_PROGRESS", 1, Integer::sum);
            return;
        }

        tournamentStatusCache.remove(link);
        stats.merge("ЗАВЕРШЁН", 1, Integer::sum);

        List<PlayerNotification> notifications = repo.findByTournamentLink(link);
        if (notifications.isEmpty()) {
            log.warn("Турнир {} — нет уведомлений", link);
            return;
        }
        TournamentEntity t = notifications.get(0).getTournament();
        finishService.handleFinished(t, notifications, doc);
    }

    public void logSummary() {
        int inCache = stats.getOrDefault("В КЭШЕ", 0);
        int inProgress = stats.getOrDefault("IN_PROGRESS", 0);
        int finished = stats.getOrDefault("ЗАВЕРШЁН", 0);
        log.info("📊 Финиш: в кэше={}, in_progress={}, завершено={}",
                inCache, inProgress, finished);
    }

    public void clearStats() {
        stats.clear();
    }
}