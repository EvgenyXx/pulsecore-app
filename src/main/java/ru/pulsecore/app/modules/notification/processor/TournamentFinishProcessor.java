package ru.pulsecore.app.modules.notification.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.integration.DocumentLoader;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.notification.finish.TournamentFinishNotificationService;
import ru.pulsecore.app.modules.notification.finish.TournamentFinishService;
import ru.pulsecore.app.modules.notification.repository.PlayerNotificationRepository;
import ru.pulsecore.app.modules.tournament.domain.TournamentStatus;
import ru.pulsecore.app.modules.tournament.parser.TournamentStatusParser;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentRepository;

import java.util.List;

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

    public Result process(String link, List<PlayerNotification> notifications) {
        if (link == null || notifications == null || notifications.isEmpty()) return null;

        TournamentEntity t = getTournament(notifications);
        if (t == null || t.isProcessed()) return null;

        Document doc = documentLoader.load(link);
        return processByStatus(t, notifications, doc);
    }

    private TournamentEntity getTournament(List<PlayerNotification> notifications) {
        return notifications.stream()
                .map(PlayerNotification::getTournament)
                .findFirst()
                .orElse(null);
    }

    private Result processByStatus(TournamentEntity t, List<PlayerNotification> notifications, Document doc) {
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);

        if (status == TournamentStatus.CANCELLED) {
            return handleCancelled(t, notifications);
        }

        if (!tournamentRepository.existsById(t.getId())) {
            log.warn("⚠️ Турнир {} (ID={}) не найден в БД, пропускаем обработку", t.getExternalId(), t.getId());
            return null;
        }

        return finishService.handleFinished(t, notifications, doc) ? new Result(true, false) : null;
    }

    private Result handleCancelled(TournamentEntity t, List<PlayerNotification> notifications) {
        if (t.isCancelled()) return new Result(false, true);

        t.setCancelled(true);
        t.setProcessed(true);
        tournamentRepository.save(t);

        notificationService.sendCancelled(notifications);
        repo.saveAll(notifications);

        log.info("❌ tournament cancelled: id={}, users={}", t.getExternalId(), notifications.size());
        return new Result(false, true);
    }

    public record Result(boolean finished, boolean cancelled) {}
}