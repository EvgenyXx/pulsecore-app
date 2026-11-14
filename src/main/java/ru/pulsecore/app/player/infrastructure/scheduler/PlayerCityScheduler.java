package ru.pulsecore.app.player.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.player.PlayerCityService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerCityScheduler {

    private final PlayerCityService playerCityService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Синхронизация городов игроков при запуске приложения");
        playerCityService.syncUpcomingCities();
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void runNightly() {
        log.info("Плановая синхронизация городов игроков (03:00)");
        playerCityService.syncUpcomingCities();
    }
}