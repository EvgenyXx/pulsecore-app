package ru.pulsecore.app.modules.notification.start;

import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.notification.processor.TournamentProcessor;
import ru.pulsecore.app.modules.notification.repository.PlayerNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentStartScheduler {

    private final PlayerNotificationRepository repo;
    private final TournamentProcessor processor;
    private static final long REQUEST_DELAY_MS = 3000;

    @Scheduled(fixedRate = 180000)
    @Transactional
    public void checkStart() {
        List<PlayerNotification> notifications = repo.findPendingWithTournament();

        log.info("🔄 StartScheduler tick: totalNotifications={}", notifications.size());

        Map<String, List<PlayerNotification>> grouped = notifications.stream()
                .filter(p -> p.getTournament() != null)
                .collect(Collectors.groupingBy(p -> p.getTournament().getLink()));

        int processed = 0;
        List<String> links = List.copyOf(grouped.keySet());

        for (int i = 0; i < links.size(); i++) {
            String link = links.get(i);
            processor.process(link, grouped.get(link));
            processed++;

            if (i < links.size() - 1) {
                try { Thread.sleep(REQUEST_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }

        log.info("✅ StartScheduler done: processed={}", processed);
    }
}