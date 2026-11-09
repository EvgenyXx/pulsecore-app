package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.application.factory.TournamentFactory;
import ru.pulsecore.app.tournament.application.match.TournamentMatchService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultPersistence;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultResolver;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentSingleProcessor {

    private final TournamentRepository tournamentRepository;
    private final TournamentNotificationService notificationService;
    private final TournamentStatusService statusService;
    private final TournamentResultResolver resultProcessor;
    private final TournamentMatchService matchService;
    private final TournamentResultPersistence tournamentResultPersistence;
    private final TournamentFactory tournamentFactory;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleUrl(ParsedResult parsed,
                                 String url, UUID playerId,
                                 String playerName) {

        TournamentEntity tournament = tournamentRepository.findByLink(url)
                .orElseGet(() -> tournamentRepository.save(tournamentFactory.createFromParsed(parsed, url)));

        if (parsed.status() == TournamentStatus.CANCELLED) {
            statusService.markAsCancelled(tournament, parsed, url);
            notificationService.createIfAbsent(playerId, tournament);
            return;
        }

        if (parsed.status() != TournamentStatus.FINISHED) return;

        notificationService.createIfAbsent(playerId, tournament);

        List<TournamentResultEntity> entities = resultProcessor.processResults(parsed.results(), playerId,
                playerName, tournament,
                parsed.nightBonus(),
                parsed.isFinished() || parsed.isFinalRemoved(),
                parsed.hasRemoved(),
                parsed.league());

        tournamentResultPersistence.saveResults(entities);
        matchService.createMatches(parsed, tournament);


    }


}