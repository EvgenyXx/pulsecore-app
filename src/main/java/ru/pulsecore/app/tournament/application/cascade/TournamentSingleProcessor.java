package ru.pulsecore.app.tournament.application.cascade;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.application.match.TournamentMatchService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentResultProcessor;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentSingleProcessor {

    private final TournamentRepository tournamentRepository;
    private final TournamentNotificationService notificationService;
    private final TournamentStatusService statusService;
    private final TournamentResultProcessor resultProcessor;
    private final TournamentMatchService matchService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TournamentResultEntity> processSingleUrl(ParsedResult parsed, String url, UUID playerId, String playerName) {
        TournamentEntity tournament = tournamentRepository.findByLink(url)
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

        if (parsed.status() == TournamentStatus.CANCELLED) {
            statusService.markAsCancelled(tournament, parsed, url);
        }

        notificationService.createIfAbsent(playerId, tournament);

        List<TournamentResultEntity> entities = resultProcessor.processResults(
                parsed.results(), playerId, playerName, tournament,
                parsed.nightBonus(),
                parsed.isFinished() || parsed.isFinalRemoved(),
                parsed.hasRemoved(),
                parsed.league());

        matchService.createMatches(parsed, tournament);

        return entities;
    }
}