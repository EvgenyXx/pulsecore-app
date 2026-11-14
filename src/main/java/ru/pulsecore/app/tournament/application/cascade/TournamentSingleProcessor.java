package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.factory.TournamentFactory;
import ru.pulsecore.app.tournament.application.match.TournamentMatchBuilder;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultResolver;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentSyncException;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentSingleProcessor {

    private final TournamentRepository tournamentRepository;
    private final TournamentNotificationService notificationService;
    private final TournamentStatusService statusService;
    private final TournamentResultResolver resultResolver;
    private final TournamentMatchBuilder matchBuilder;
    private final TournamentFactory tournamentFactory;

    public ProcessedTournament processSingleUrl(ParsedResult parsed, String url, UUID playerId, String playerName) {

        TournamentEntity tournament = findOrCreate(parsed, url);

        if (parsed.status() == TournamentStatus.CANCELLED) {
            statusService.markAsCancelled(tournament, parsed, url);
            notificationService.createIfAbsent(playerId, tournament);
            return new ProcessedTournament(tournament, List.of(), Optional.empty());
        }

        if (parsed.status() != TournamentStatus.FINISHED) {
            notificationService.createIfAbsent(playerId, tournament);
            return new ProcessedTournament(tournament, List.of(), Optional.empty());
        }

        notificationService.createIfAbsent(playerId, tournament);

        List<TournamentMatchEntity> matches = matchBuilder.buildMatches(parsed, tournament);
        Optional<TournamentResultEntity> result = resultResolver.processResults(
                parsed.results(), playerId, playerName, tournament,
                parsed.nightBonus(), parsed.isFinished() || parsed.isFinalRemoved(),
                parsed.hasRemoved(), parsed.league()
        );

        return new ProcessedTournament(tournament, matches, result);
    }


    private TournamentEntity findOrCreate(ParsedResult parsed, String url) {
        try {
            return tournamentRepository.findByLink(url)
                    .orElseGet(() -> tournamentRepository.save(
                            tournamentFactory.createFromParsed(parsed, url)
                    ));
        } catch (DataIntegrityViolationException e) {
            log.warn("Дубликат externalId={}, url={}", parsed.tournamentId(), url);
            return tournamentRepository.findByExternalId(parsed.tournamentId())
                    .orElseThrow(() -> new TournamentSyncException(
                            "Турнир не найден после конфликта: externalId=" + parsed.tournamentId()));
        }
    }
}