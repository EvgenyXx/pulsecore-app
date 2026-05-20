package ru.pulsecore.app.modules.notification.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.notification.processor.TournamentFinishProcessor;
import ru.pulsecore.app.modules.notification.repository.PlayerNotificationRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishScheduler {

    private final PlayerNotificationRepository repo;
    private final TournamentFinishProcessor processor;
    private static final long REQUEST_DELAY_MS = 3000;

    @Scheduled(fixedRate = 420000)
    public void checkFinished() {

        List<PlayerNotification> list = repo.findNotFinishedFull();

        if (list.isEmpty()) {
            log.debug("FinishScheduler: no pending notifications");
            return;
        }

        Map<String, List<PlayerNotification>> grouped = list.stream()
                .filter(p -> p.getTournament() != null)
                .collect(Collectors.groupingBy(p -> p.getTournament().getLink()));

        int processed = 0;
        int finished = 0;
        int cancelled = 0;

        List<String> links = List.copyOf(grouped.keySet());
        for (int i = 0; i < links.size(); i++) {
            String link = links.get(i);
            TournamentFinishProcessor.Result result = processor.process(link, grouped.get(link));

            if (result == null) continue;

            processed++;
            if (result.finished()) finished++;
            if (result.cancelled()) cancelled++;

            if (i < links.size() - 1) {
                try { Thread.sleep(REQUEST_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }

        if (processed > 0) {
            log.info("FinishScheduler: processed {} tournaments, finished={}, cancelled={}",
                    processed, finished, cancelled);
        }
    }
}