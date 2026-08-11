package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

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
    private final PlayerNotificationRepository repo;



    @Transactional
    public void processFinish() {
        List<PlayerNotification> all = repo.findStartedNotFinished();
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
        finishService.handleFinished(t, notifications, doc);
    }

    private TournamentEntity getTournament(List<PlayerNotification> notifications) {
        return notifications.stream()
                .map(PlayerNotification::getTournament)
                .findFirst()
                .orElse(null);
    }


}