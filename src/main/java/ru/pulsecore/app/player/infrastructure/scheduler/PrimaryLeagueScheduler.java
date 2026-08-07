
package ru.pulsecore.app.player.infrastructure.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.player.PrimaryLeagueService;

@Service
@RequiredArgsConstructor
public class PrimaryLeagueScheduler {

    private final PrimaryLeagueService primaryLeagueService;

    @PostConstruct
    public void init() {
        updateAllPrimaryLeagues();
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void updateAllPrimaryLeagues() {
      primaryLeagueService.updatePrimaryLeague();
    }
}