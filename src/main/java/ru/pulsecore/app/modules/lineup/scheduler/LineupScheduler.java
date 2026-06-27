package ru.pulsecore.app.modules.lineup.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.lineup.service.LineupService;

@Slf4j
@Component
@RequiredArgsConstructor
public class LineupScheduler implements ApplicationRunner {

    private final LineupService lineupService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initial lineup load on startup");
        lineupService.loadLineups();
    }


    @Scheduled(cron = "0 */10 * * * *")
    public void loadToday() {
        lineupService.loadTodayOnly();
    }


    @Scheduled(cron = "0 */20 * * * *")
    public void loadTomorrow() {
        lineupService.loadTomorrowOnly();
    }


    @Scheduled(cron = "0 0 */3 * * *")
    public void loadDayAfterTomorrow() {
        lineupService.loadDayAfterTomorrow();
    }


    @Scheduled(cron = "0 0 4 * * *")
    public void cleanup() {
        lineupService.cleanupOld();
    }
}