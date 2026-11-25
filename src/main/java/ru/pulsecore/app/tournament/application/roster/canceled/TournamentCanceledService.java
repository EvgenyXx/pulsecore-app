package ru.pulsecore.app.tournament.application.roster.canceled;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;
import ru.pulsecore.app.tournament.application.resolution.BrokenUriService;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;
import ru.pulsecore.app.tournament.infrastructure.exception.PageNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentParser;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentStatusParser;


@Service
@Slf4j
public class TournamentCanceledService {


    private final JsonTournamentParser jsonTournamentParser;
    private final JsonTournamentStatusParser jsonTournamentStatusParser;
    private final TournamentCancellationService tournamentCancellationService;
    private final DocumentLoader documentLoader;
    private final Bucket canceledRateLimiter;
    private final BrokenUriService brokenUriService;
    private final TournamentCanceledAuditor canceledAuditor;

    public TournamentCanceledService(
            JsonTournamentParser jsonTournamentParser,
            JsonTournamentStatusParser jsonTournamentStatusParser,
            TournamentCancellationService tournamentCancellationService,
            DocumentLoader documentLoader,
            @Qualifier(RateLimiterConfig.CANCELED_RATE_LIMITER) Bucket canceledRateLimiter,
            BrokenUriService brokenUriService, TournamentCanceledAuditor canceledAuditor) {
        this.jsonTournamentParser = jsonTournamentParser;
        this.jsonTournamentStatusParser = jsonTournamentStatusParser;
        this.tournamentCancellationService = tournamentCancellationService;
        this.documentLoader = documentLoader;
        this.canceledRateLimiter = canceledRateLimiter;
        this.brokenUriService = brokenUriService;
        this.canceledAuditor = canceledAuditor;
    }


    @Async(AsyncConfig.CANCELED_EXECUTOR)
    public void processLink(String url) {
        canceledRateLimiter.asBlocking().consumeUninterruptibly(1);
        try {
            Document doc = documentLoader.load(url);
            processByStatus(doc);
        } catch (PageNotFoundException e) {
            log.warn("Пришла битая ссылка:{}", url);
            brokenUriService.handle(url);
        }
    }

    private void processByStatus(Document doc) {
        TournamentPage page = jsonTournamentParser.parse(doc);

        TournamentStatus status = jsonTournamentStatusParser.parseStatus(page);

        if (status == TournamentStatus.CANCELLED) {
            log.warn("Туринр отменен:{}", doc.baseUri());
            String url = doc.baseUri();
            tournamentCancellationService.handleCancelled(url);
            canceledAuditor.logCancelled(url);
        }
    }
}