package ru.pulsecore.app.tournament.application.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentStatusParser;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.application.notification.TournamentFinishNotificationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Обрабатывает завершенные и отмененные турниры.
 * Вызывается шедулером каждые 7 минут.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishProcessor {

    private final DocumentLoader documentLoader;
    private final TournamentFinishService finishService;
    private final TournamentFinishNotificationService notificationService;
    private final PlayerNotificationRepository repo;
    private final TournamentRepository tournamentRepository;
    private final TournamentStatusParser tournamentStatusParser;


    @Transactional
    public void processFinish() {
        List<PlayerNotification> all = repo.findNotFinishedFull();
        if (all.isEmpty()) return;

        Map<String, List<PlayerNotification>> grouped = all.stream()
                .filter(p -> p.getTournament() != null)
                .collect(Collectors.groupingBy(p -> p.getTournament().getLink()));

        log.info("Наблюдаются {} турниров:", grouped.size());
        grouped.forEach((link, notifications) -> {
            TournamentEntity t = notifications.get(0).getTournament();
            log.info("  {} {} | {} | {}", t.getDate(), t.getTime(), link, notifications.size());
        });

        for (var entry : grouped.entrySet()) {
            process(entry.getKey(), entry.getValue());
        }
    }
    private void process(String link, List<PlayerNotification> notifications) {
        TournamentEntity t = getTournament(notifications);
        if (t == null || t.isProcessed()) return;

        Document doc = documentLoader.load(link);
        processByStatus(t, notifications, doc);
    }

    private TournamentEntity getTournament(List<PlayerNotification> notifications) {
        return notifications.stream()
                .map(PlayerNotification::getTournament)
                .findFirst()
                .orElse(null);
    }

    private void processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);

        if (status == TournamentStatus.CANCELLED) {
            handleCancelled(t, notifications);
            return;
        }

        if (!tournamentRepository.existsById(t.getId())) {
            log.warn("⚠️ Турнир {} (ID={}) не найден в БД, пропускаем обработку", t.getExternalId(), t.getId());
            return;
        }

        finishService.handleFinished(t, notifications, doc);
    }

    private void handleCancelled(TournamentEntity t, List<PlayerNotification> notifications) {
        if (t.isCancelled()) return;

        t.setStarted(true);
        t.setCancelled(true);
        t.setProcessed(true);
        t.setFinished(true);
        tournamentRepository.save(t);

        notificationService.sendCancelled(notifications);
        repo.saveAll(notifications);

        log.info("❌ tournament cancelled: id={}, users={}", t.getExternalId(), notifications.size());
    }
}