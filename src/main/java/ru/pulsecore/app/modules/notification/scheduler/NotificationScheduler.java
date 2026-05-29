package ru.pulsecore.app.modules.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.notification.discovery.TournamentDiscoveryService;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final PlayerService playerService;
    private final TournamentDiscoveryService discoveryService;

    @Scheduled(fixedDelay = 900000)
    public void checkAllUsers() {
        List<Player> players = playerService.getAll();

        if (players.isEmpty()) {
            log.debug("Scheduler: no players to check");
            return;
        }

        int errors = 0;
        int skipped = 0;

        for (Player player : players) {
            try {
                if (!player.hasActiveSubscription()) {
                    skipped++;
                    continue;
                }
                discoveryService.checkNewTournaments(player.getId());
            } catch (Exception e) {
                errors++;
                log.error("Failed to check tournaments for player {}", player.getId(), e);
            }
        }

        if (skipped > 0) {
            log.info("Scheduler: skipped {} players without active subscription", skipped);
        }
        if (errors > 0) {
            log.warn("Scheduler completed with {} errors out of {} players", errors, players.size());
        }
    }
}