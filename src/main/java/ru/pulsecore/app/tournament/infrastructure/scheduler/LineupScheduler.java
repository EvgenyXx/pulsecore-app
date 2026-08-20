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
import ru.pulsecore.app.tournament.infrastructure.circuit.MastersApiCircuitBreaker;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * Шедулер загрузки и очистки составов.
 *
 * <p>При старте приложения загружает составы на 3 дня (сегодня, завтра, послезавтра)
 * с паузами между запросами, чтобы не создавать нагрузку на API.</p>
 *
 * <p>Далее по расписанию обновляет данные:</p>
 * <ul>
 *     <li>Сегодня — каждые 30 минут</li>
 *     <li>Завтра — каждые 30 минут, со смещением 5 минут</li>
 *     <li>Послезавтра — каждые 3 часа, со смещением 10 минут</li>
 *     <li>Очистка старых составов — каждые 15 минут</li>
 * </ul>
 *
 * <p>Смещения initialDelay гарантируют, что задачи не накладываются друг на друга.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LineupScheduler implements ApplicationRunner {

    private final LineupUpsertService lineupUpsertService;
    private final LineupCleanupService lineupCleanupService;
    private final MastersApiCircuitBreaker circuitBreaker;

    @Override
    public void run(ApplicationArguments args) {
        if (circuitBreaker.isBlocked()) return;
        try {
            TimeUnit.MINUTES.sleep(3);
            LocalDate today = LocalDate.now();
            lineupUpsertService.loadDay(today);
            TimeUnit.SECONDS.sleep(30);
            lineupUpsertService.loadDay(today.plusDays(1));
            TimeUnit.SECONDS.sleep(60);
            lineupUpsertService.loadDay(today.plusDays(2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Прервана загрузка составов при старте");
        } catch (Exception e) {
            log.error("Ошибка загрузки составов при старте: {}", e.getMessage());
        }
    }

    @Scheduled(
            initialDelay =30 ,
            fixedDelay = 90,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void loadToday() {
        if (circuitBreaker.isBlocked()) return;
        lineupUpsertService.loadDay(LocalDate.now());
    }
    @Scheduled(
            initialDelay = 20,
            fixedDelay = 30,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void loadTomorrow() {
        if (circuitBreaker.isBlocked()) return;
        lineupUpsertService.loadDay(LocalDate.now().plusDays(1));
    }

    @Scheduled(
            initialDelay = 10,
            fixedDelay = 180,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void loadDayAfterTomorrow() {
        if (circuitBreaker.isBlocked()) return;
        lineupUpsertService.loadDay(LocalDate.now().plusDays(2));
    }

    @Scheduled(
            initialDelay = 50,
            fixedDelay = 15,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void cleanup() {
        lineupCleanupService.cleanupOld();
    }
}