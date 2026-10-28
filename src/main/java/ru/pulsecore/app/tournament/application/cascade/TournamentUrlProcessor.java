package ru.pulsecore.app.tournament.application.cascade;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.resolution.ResultService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultProcessor;
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
    private final TournamentResultProcessor resultProcessor;
    private final Bucket tournamentRateLimiter;

    public TournamentUrlProcessor(ResultService resultService,
                                  TournamentSingleProcessor singleProcessor,
                                  TournamentResultProcessor resultProcessor,
                                  @Qualifier(RateLimiterConfig.HISTORY_SYNC_RATE_LIMITER) Bucket tournamentRateLimiter) {
        this.resultService = resultService;
        this.singleProcessor = singleProcessor;
        this.resultProcessor = resultProcessor;
        this.tournamentRateLimiter = tournamentRateLimiter;
    }

    public void processUrlsForPlayer(List<String> urls, UUID playerId, String playerName) {
        List<TournamentResultEntity> allEntities = new ArrayList<>();

        for (String url : urls) {
            try {
                tournamentRateLimiter.asBlocking().consume(1);
                ParsedResult parsed = resultService.calculateAll(url);

                List<TournamentResultEntity> entities = singleProcessor.processSingleUrl(parsed, url, playerId, playerName);
                allEntities.addAll(entities);

            } catch (Exception e) {
                if (e instanceof DataIntegrityViolationException) {
                    log.warn("Дубликат турнира, пропущен: {}", url);
                } else {
                    log.error("Ошибка обработки URL: {}", url, e);
                }
            }
        }

        resultProcessor.saveAll(allEntities);
    }
}