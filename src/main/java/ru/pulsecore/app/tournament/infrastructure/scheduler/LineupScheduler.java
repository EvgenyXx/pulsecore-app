package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.lineup.LineupCleanupService;
import ru.pulsecore.app.tournament.application.lineup.LineupUpsertService;

import java.time.LocalDate;

/**
 * Шедулер загрузки и очистки составов.
 * При старте загружает составы на 3 дня.
 * Далее обновляет сегодня/завтра/послезавтра по крону.
 * Раз в минуту чистит старые составы.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LineupScheduler implements ApplicationRunner {

    private final LineupUpsertService lineupUpsertService;
    private final LineupCleanupService lineupCleanupService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Thread.sleep(10_000);
            LocalDate today = LocalDate.now();
            lineupUpsertService.loadDay(today);
            Thread.sleep(3_000);
            lineupUpsertService.loadDay(today.plusDays(1));
            Thread.sleep(3_000);
            lineupUpsertService.loadDay(today.plusDays(2));
        } catch (Exception e) {
            log.error("Ошибка загрузки составов при старте: {}", e.getMessage());
        }
    }


    @Scheduled(initialDelay = 120_000, fixedRateString = "PT10M", scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void loadToday() {
        lineupUpsertService.loadDay(LocalDate.now());
    }

    @Scheduled(cron = "30 */20 * * * *", scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void loadTomorrow() {
        lineupUpsertService.loadDay(LocalDate.now().plusDays(1));
    }

    @Scheduled(cron = "0 5 */3 * * *", scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void loadDayAfterTomorrow() {
        lineupUpsertService.loadDay(LocalDate.now().plusDays(2));
    }

    @Scheduled(initialDelay = 180_000, fixedRate = 60000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void cleanup() {
        lineupCleanupService.cleanupOld();
    }
}