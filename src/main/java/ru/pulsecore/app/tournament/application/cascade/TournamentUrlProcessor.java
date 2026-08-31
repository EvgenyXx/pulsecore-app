package ru.pulsecore.app.tournament.application.cascade;

import io.github.bucket4j.Bucket;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.event.TournamentMatchService;
import ru.pulsecore.app.tournament.application.resolution.ResultService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultProcessor;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentParseException;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.time.LocalDate;
import java.util.*;


/**
 * Обрабатывает список URL турниров для одного игрока.
 *
 * <p>Для каждого URL:</p>
 * <ol>
 *     <li>Парсит страницу турнира</li>
 *     <li>Находит существующий турнир или создаёт новый</li>
 *     <li>Если турнир отменён — помечает его в базе</li>
 *     <li>Создаёт связь игрока с турниром, если её нет</li>
 *     <li>Собирает результаты игроков</li>
 * </ol>
 *
 * <p>Все результаты сохраняются одним батчем в конце обработки.</p>
 *
 * <p>Вызывается из {@code TournamentAutoAddService} при синхронизации истории.</p>
 */
@Service
@Slf4j
public class TournamentUrlProcessor {

    private final TournamentResultProcessor resultProcessor;
    private final ResultService resultService;
    private final TournamentRepository tournamentRepository;
    private final TournamentNotificationService notificationService;
    private final TournamentStatusService statusService;
    private final Bucket tournamentRateLimiter;
    private final TournamentMatchService matchService;


    public TournamentUrlProcessor(TournamentResultProcessor resultProcessor,
                                  ResultService resultService,
                                  TournamentRepository tournamentRepository,
                                  TournamentNotificationService notificationService,
                                  TournamentStatusService statusService,
                                  @Qualifier(RateLimiterConfig.HISTORY_SYNC_RATE_LIMITER) Bucket tournamentRateLimiter,
                                  TournamentMatchService matchService) {
        this.resultProcessor = resultProcessor;
        this.resultService = resultService;
        this.tournamentRepository = tournamentRepository;
        this.notificationService = notificationService;
        this.statusService = statusService;
        this.tournamentRateLimiter = tournamentRateLimiter;
        this.matchService = matchService;

    }

    @Transactional
    public void processUrlsForPlayer(List<String> urls, UUID playerId, String playerName) {
        List<TournamentResultEntity> allEntities = new ArrayList<>();

        for (String url : urls) {
            try {
                tournamentRateLimiter.asBlocking().consume(1);
                ParsedResult parsed = parseUrl(url);
                TournamentEntity tournament = findOrCreateTournament(parsed, url);
                updateTournamentStatus(tournament,parsed,url);
                notificationService.createIfAbsent(playerId, tournament);

                allEntities.addAll(resultProcessor.processResults(
                        parsed.results(), playerId, playerName, tournament,
                        parsed.nightBonus(),
                        parsed.isFinished() || parsed.isFinalRemoved(),
                        parsed.hasRemoved(),
                        parsed.league()));

                matchService.createMatches(parsed,tournament);
            } catch (Exception e) {
                log.warn("{} — {}", url, e.getMessage());
            }
        }

        resultProcessor.saveAll(allEntities);
    }


    private void updateTournamentStatus(TournamentEntity tournament, ParsedResult parsed, String url) {
        if (parsed.status() == TournamentStatus.CANCELLED) {
            statusService.markAsCancelled(tournament, parsed, url);
        }
    }

    private ParsedResult parseUrl(String url) {
        try {
            return resultService.calculateAll(url);
        } catch (Exception e) {
            throw new TournamentParseException(e);
        }
    }

    private TournamentEntity findOrCreateTournament(ParsedResult parsed, String url) {
        return tournamentRepository.findByLink(url)
                .orElseGet(() -> tournamentRepository.save(TournamentEntity.builder()
                        .externalId(parsed.tournamentId())
                        .time(parsed.time())
                        .cancelled(false)
                        .finished(true)
                        .processed(true)
                        .started(true)
                        .date(parsed.date() != null ? LocalDate.parse(parsed.date()) : null)
                        .link(url)
                        .build()));
    }
}