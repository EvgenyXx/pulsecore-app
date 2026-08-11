package ru.pulsecore.app.tournament.application.roster.start;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.shared.exception.SiteUnavailableException;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentStatusParser;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentStartProcessor {

    private static final long REQUEST_DELAY_MS = 3000;

    private final DocumentLoader documentLoader;
    private final TournamentTimeService timeService;
    private final PlayerNotificationRepository notificationRepository;
    private final TournamentStatusParser tournamentStatusParser;

   
    @Transactional
    public void checkStart() {
        List<PlayerNotification> notifications = notificationRepository.findPendingWithTournament();
        if (notifications.isEmpty()) return;

        Map<String, List<PlayerNotification>> grouped = notifications.stream()
                .filter(p -> p.getTournament() != null)
                .collect(Collectors.groupingBy(p -> p.getTournament().getLink()));

        List<String> links = List.copyOf(grouped.keySet());
        for (int i = 0; i < links.size(); i++) {
            process(links.get(i), grouped.get(links.get(i)));
            if (i < links.size() - 1) {
                try {
                    TimeUnit.MILLISECONDS.sleep(REQUEST_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void process(String link, List<PlayerNotification> notifications) {
        TournamentEntity t = getTournament(notifications);
        if (t == null) return;

        try {
            Document doc = documentLoader.load(link);
            processByStatus(t, notifications, doc);
        } catch (SiteUnavailableException e) {
            log.warn("Сайт недоступен для турнира: link={}", link);
        } catch (Exception e) {
            log.error("Ошибка обработки турнира: link={}", link, e);
        }
    }

    private TournamentEntity getTournament(List<PlayerNotification> notifications) {
        PlayerNotification first = notifications.get(0);
        return first != null ? first.getTournament() : null;
    }

    private void processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);

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
        notificationRepository.saveAll(notifications);
        log.info("Турнир начался. Дата:{} Время:{} URL:{} ",t.getDate(),t.getTime(),t.getLink());
    }


}