package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.TournamentLinkResult;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentStatus;
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

    public TournamentLinkResult process(String link, Player player) throws Exception {

        ParsedResult parsed = resultService.calculateAll(link);

        if (parsed.results() == null || parsed.results().isEmpty()) {
            return result(TournamentLinkStatus.NOT_STARTED, parsed);
        }

        boolean userExists = participationService.isUserInParsed(parsed, player.getName());
        if (!userExists) {
            return result(TournamentLinkStatus.NOT_PARTICIPATING, parsed);
        }

        TournamentEntity tournament = tournamentRepository.findByLink(link).orElse(null);
        if (tournament != null) {
            if (!tournament.isProcessed()) {
                return result(TournamentLinkStatus.ALREADY_TRACKED, parsed);
            }
            return result(TournamentLinkStatus.FINISHED, parsed);
        }

        tournament = tournamentSyncService.sync(parsed, link);

        tournamentResultService.processResults(
                parsed.results(),
                player,
                tournament,
                parsed.nightBonus(),
                parsed.status() == TournamentStatus.FINISHED,
                parsed.hasRemoved(),
                parsed.league()  // ← добавить
        );

        if (parsed.status() == TournamentStatus.FINISHED) {
            tournament.setProcessed(true);
            return result(TournamentLinkStatus.FINISHED, parsed);
        }

        return result(TournamentLinkStatus.TRACKING_STARTED, parsed);
    }

    private TournamentLinkResult result(TournamentLinkStatus status,
                                        ParsedResult parsed) {
        log.debug("TournamentLink status={}", status);
        return new TournamentLinkResult(status, parsed);
    }
}