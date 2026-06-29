package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.TournamentLinkResult;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentStatus;
import ru.pulsecore.app.modules.tournament.exception.TournamentProcessException;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentLinkStatus;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TournamentLinkService {

    private final ResultService resultService;
    private final TournamentResultService tournamentResultService;
    private final TournamentSyncService tournamentSyncService;
    private final TournamentRepository tournamentRepository;
    private final ParticipationService participationService;

    public TournamentLinkResult process(String link, Player player) {
        ParsedResult parsed = calculateResult(link);

        if (hasNoResults(parsed)) {
            return result(TournamentLinkStatus.NOT_STARTED, parsed);
        }

        if (!participationService.isUserInParsed(parsed, player.getName())) {
            return result(TournamentLinkStatus.NOT_PARTICIPATING, parsed);
        }

        return processExistingOrNew(link, player, parsed);
    }

    private boolean hasNoResults(ParsedResult parsed) {
        return parsed.results() == null || parsed.results().isEmpty();
    }

    private TournamentLinkResult processExistingOrNew(String link, Player player, ParsedResult parsed) {
        TournamentEntity tournament = tournamentRepository.findByLink(link).orElse(null);

        if (tournament != null) {
            return tournament.isProcessed()
                    ? result(TournamentLinkStatus.FINISHED, parsed)
                    : result(TournamentLinkStatus.ALREADY_TRACKED, parsed);
        }

        tournament = tournamentSyncService.sync(parsed, link);
        processPlayerResults(player, tournament, parsed);

        if (parsed.status() == TournamentStatus.FINISHED) {
            tournament.setProcessed(true);
            return result(TournamentLinkStatus.FINISHED, parsed);
        }

        return result(TournamentLinkStatus.TRACKING_STARTED, parsed);
    }

    private void processPlayerResults(Player player, TournamentEntity tournament, ParsedResult parsed) {
        tournamentResultService.processResults(
                parsed.results(),
                player,
                tournament,
                parsed.nightBonus(),
                parsed.status() == TournamentStatus.FINISHED,
                parsed.hasRemoved(),
                parsed.league()
        );
    }

    private ParsedResult calculateResult(String link) {
        try {
            return resultService.calculateAll(link);
        } catch (Exception e) {
            throw new TournamentProcessException("Failed to process tournament: " + link, e);
        }
    }

    private TournamentLinkResult result(TournamentLinkStatus status, ParsedResult parsed) {
        log.debug("TournamentLink status={}", status);
        return new TournamentLinkResult(status, parsed);
    }
}