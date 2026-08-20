package ru.pulsecore.app.tournament.application.roster.canceled;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.tournament.application.resolution.BrokenUriService;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;
import ru.pulsecore.app.tournament.infrastructure.exception.PageNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentStatusParser;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Сервис проверки отмены турниров.
 *
 * <p>Каждый турнир обрабатывается асинхронно в пуле {@link AsyncConfig#CANCELED_EXECUTOR}.
 * Метод возвращает {@link CompletableFuture} для возможности ожидания завершения всех задач.</p>
 */
@Service
@Slf4j
public class TournamentCanceledService {

    private final TournamentRepository tournamentRepository;
    private final TournamentStatusParser tournamentStatusParser;
    private final TournamentCancellationService tournamentCancellationService;
    private final PlayerNotificationRepository notificationRepository;
    private final DocumentLoader documentLoader;
    private final Bucket canceledRateLimiter;
    private final BrokenUriService brokenUriService;
    private final Map<String, Integer> stats = new ConcurrentHashMap<>();


    public TournamentCanceledService(TournamentRepository tournamentRepository,
                                     TournamentStatusParser tournamentStatusParser
            , TournamentCancellationService tournamentCancellationService,
                                     PlayerNotificationRepository notificationRepository,
                                     DocumentLoader documentLoader,
                                     @Qualifier(RateLimiterConfig.CANCELED_RATE_LIMITER) Bucket canceledRateLimiter,
                                     BrokenUriService brokenUriService) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentStatusParser = tournamentStatusParser;
        this.tournamentCancellationService = tournamentCancellationService;
        this.notificationRepository = notificationRepository;
        this.documentLoader = documentLoader;
        this.canceledRateLimiter = canceledRateLimiter;
        this.brokenUriService = brokenUriService;
    }



    @Async(AsyncConfig.CANCELED_EXECUTOR)
    public CompletableFuture<Void> processLink(String link) {
        stats.merge("всего", 1, Integer::sum);

        List<PlayerNotification> notifications = notificationRepository.findByTournamentLink(link);
        if (notifications.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        TournamentEntity t = notifications.get(0).getTournament();
        if (t == null || t.isProcessed()) {
            return CompletableFuture.completedFuture(null);
        }

        canceledRateLimiter.asBlocking().consumeUninterruptibly(1);
        try {
            Document doc = documentLoader.load(link);
            processByStatus(t, notifications, doc);
        } catch (PageNotFoundException e) {
            log.error("Битая ссылка {}", link);
            Set<UUID> playerIds = notifications.stream()
                    .map(PlayerNotification::getPlayerId)
                    .collect(Collectors.toSet());
            brokenUriService.handle(t, playerIds);
            stats.merge("битые", 1, Integer::sum);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);


        if (status == TournamentStatus.CANCELLED) {
            tournamentCancellationService.handleCancelled(t, notifications);
            stats.merge("отменено", 1, Integer::sum);
            return;
        }

        if (!tournamentRepository.existsById(t.getId())) {
            log.warn("Турнир {} (ID={}) не найден в БД", t.getExternalId(), t.getId());
        }
    }


    public void logSummary() {
        int total = stats.getOrDefault("всего", 0);
        int cancelled = stats.getOrDefault("отменено", 0);
        int broken = stats.getOrDefault("битые", 0);
        log.info("📊 Итог: всего={}, отменено={}, битые={}", total, cancelled, broken);
    }

    public void clearStats() {
        stats.clear();
    }
}