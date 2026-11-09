package ru.pulsecore.app.tournament.application.cascade;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.resolution.ResultService;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TournamentUrlProcessor {

    private final ResultService resultService;
    private final TournamentSingleProcessor singleProcessor;
    private final Bucket tournamentRateLimiter;

    public TournamentUrlProcessor(ResultService resultService,
                                  TournamentSingleProcessor singleProcessor,
                                  @Qualifier(RateLimiterConfig.HTML_PARSE_RATE_LIMITER) Bucket tournamentRateLimiter) {
        this.resultService = resultService;
        this.singleProcessor = singleProcessor;
        this.tournamentRateLimiter = tournamentRateLimiter;
    }

    public void processUrlsForPlayer(List<String> urls, UUID playerId, String playerName) {
        for (String url : urls) {
            try {
                tournamentRateLimiter.asBlocking().consume(1);
                ParsedResult parsed = resultService.calculateAll(url);
                singleProcessor.processSingleUrl(parsed, url, playerId, playerName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Поток был прерван во время ожидания RateLimiter. Пропуск ссылки: {}", url);
                break;
            }
        }
    }
}