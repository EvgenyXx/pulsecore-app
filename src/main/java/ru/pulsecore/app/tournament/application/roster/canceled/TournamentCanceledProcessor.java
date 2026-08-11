package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentStatusParser;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Сервис проверки отмены турнира
 * если при сканировании будет обнаружено
 * то что статус турнира отменен
 * будет немедленно отправлено письмо
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentCanceledProcessor {

    private final TournamentRepository tournamentRepository;
    private final TournamentStatusParser tournamentStatusParser;
    private final TournamentCanceledNotificationService notificationService;
    private final PlayerNotificationRepository notificationRepository;
    private final DocumentLoader documentLoader;


    public void processCanceled() {
        List<PlayerNotification> all = notificationRepository.findNotStartedForCancel();
        log.debug("Processing {} notifications", all.size());
        all.forEach(n -> log.debug("  player={}, tournament={}",
                n.getPlayerId(), n.getTournament().getId()));
        if (all.isEmpty()) return;

        Map<String, List<PlayerNotification>> grouped = all.stream()
                .filter(p -> p.getTournament() != null)
                .collect(Collectors.groupingBy(p -> p.getTournament().getLink()));

        log.debug("Наблюдаются {} турниров:", grouped.size());
        grouped.forEach((link, notifications) -> {
            TournamentEntity t = notifications.get(0).getTournament();
            log.debug("  {} {} | {} | {}", t.getDate(), t.getTime(), link, notifications.size());
        });

        for (var entry : grouped.entrySet()) {
            process(entry.getKey(), entry.getValue());
        }
    }

    private void process(String link, List<PlayerNotification> notifications) {
        TournamentEntity t = getTournament(notifications);
        if (t == null || t.isProcessed()) return;

        Document doc = documentLoader.load(link);
        processByStatus(t,notifications,doc);
    }

    private TournamentEntity getTournament(List<PlayerNotification> notifications) {
        return notifications.stream()
                .map(PlayerNotification::getTournament)
                .findFirst()
                .orElse(null);
    }
    
    public void processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);
        log.debug("Статус: {}",status);
        if (status == TournamentStatus.CANCELLED) {
            handleCancelled(t, notifications);
            return;
        }

        if (!tournamentRepository.existsById(t.getId())) {
            log.warn("⚠️ Турнир {} (ID={}) не найден в БД, пропускаем обработку", t.getExternalId(), t.getId());
        }


    }

    private void handleCancelled(TournamentEntity t, List<PlayerNotification> notifications) {
        if (t.isCancelled()) return;

        t.setStarted(true);
        t.setCancelled(true);
        t.setProcessed(true);
        t.setFinished(true);
        tournamentRepository.save(t);

        notificationService.sendCancelled(notifications);
        notificationRepository.saveAll(notifications);

        log.info("❌ tournament cancelled: id={}, users={}", t.getExternalId(), notifications.size());
    }
}
