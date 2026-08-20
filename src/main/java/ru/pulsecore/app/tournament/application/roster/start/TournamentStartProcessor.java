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

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        String moscowTime = LocalTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("HH:mm"));

        log.debug("Старт: проверка в {}", moscowTime);

        List<String> tournamentLinks = notificationRepository.findStartingSoonLinks(moscowTime);

        if (tournamentLinks.isEmpty()) {
            log.debug("Старт: нет турниров для проверки");
            return;
        }

        log.info("Проверка старта: найдено {} турниров", tournamentLinks.size());

        for (int i = 0; i < tournamentLinks.size(); i++) {
            processLink(tournamentLinks.get(i));
            if (i < tournamentLinks.size() - 1) {
                try {
                    TimeUnit.MILLISECONDS.sleep(REQUEST_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void processLink(String link) {
        List<PlayerNotification> notifications = notificationRepository.findByTournamentLink(link);
        if (notifications.isEmpty()) {
            log.debug("Старт: нет уведомлений для link={}", link);
            return;
        }

        TournamentEntity t = notifications.get(0).getTournament();
        if (t == null) {
            log.debug("Старт: турнир не найден для link={}", link);
            return;
        }

        process(link, notifications, t);
    }

    private void process(String link, List<PlayerNotification> notifications, TournamentEntity t) {
        try {
            Document doc = documentLoader.load(link);
            processByStatus(t, notifications, doc);
        } catch (SiteUnavailableException e) {
            log.warn("Сайт недоступен для турнира: link={}", link);
        } catch (Exception e) {
            log.error("Ошибка обработки турнира: link={}", link, e);
        }
    }

    private void processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);
        log.debug("Старт: турнир={}, статус={}", t.getExternalId(), status);

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
        log.info("Турнир начался. Дата:{} Время:{} URL:{} ", t.getDate(), t.getTime(), t.getLink());
    }
}