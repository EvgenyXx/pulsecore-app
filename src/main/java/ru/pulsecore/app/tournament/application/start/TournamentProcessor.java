package ru.pulsecore.app.tournament.application.start;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.shared.exception.SiteUnavailableException;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentStatusParser;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentProcessor {

    private final DocumentLoader documentLoader;
    private final TournamentTimeService timeService;

    private final PlayerNotificationRepository repo;
    private final TournamentStatusParser tournamentStatusParser;

    public void process(String link, List<PlayerNotification> notifications) {
        if (link == null || notifications == null || notifications.isEmpty()) return;

        TournamentEntity t = getTournament(notifications);
        if (t == null) return;

        try {
            Document doc = documentLoader.load(link);
            processByStatus(t, notifications, doc);
        } catch (SiteUnavailableException e) {
            log.warn("Site unavailable for tournament: link={}", link);
        } catch (Exception e) {
            log.error("Failed to process tournament: link={}", link, e);
        }
    }

    private TournamentEntity getTournament(List<PlayerNotification> notifications) {
        PlayerNotification first = notifications.get(0);
        return first != null ? first.getTournament() : null;
    }

    private void processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);

        if (status == TournamentStatus.CANCELLED) {
            handleCancelled(t, notifications);
            return;
        }

        if (isAlreadyStartedOrNotToday(t)) return;

        if (shouldStart(t, status)) {
            startTournament(t, notifications);
        }
    }

    private boolean isAlreadyStartedOrNotToday(TournamentEntity t) {
        return t.isStarted() || !timeService.isToday(t);
    }

    private boolean shouldStart(TournamentEntity t, TournamentStatus status) {
        return status == TournamentStatus.IN_PROGRESS
                || status == TournamentStatus.FINISHED
                || timeService.isStartedByTime(t);
    }

    private void startTournament(TournamentEntity t, List<PlayerNotification> notifications) {

            t.setStarted(true);
            repo.saveAll(notifications);

    }

    private void handleCancelled(TournamentEntity t, List<PlayerNotification> notifications) {


        t.setCancelled(true);

        repo.saveAll(notifications);
    }
}