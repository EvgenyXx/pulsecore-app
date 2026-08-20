package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.roster.canceled.TournamentCanceledProcessor;
import ru.pulsecore.app.tournament.application.roster.change.TournamentChangeService;
import ru.pulsecore.app.tournament.application.roster.discovery.TournamentDiscoveryService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentFinishProcessor;
import ru.pulsecore.app.tournament.application.roster.start.TournamentStartProcessor;
import ru.pulsecore.app.tournament.infrastructure.circuit.MastersApiCircuitBreaker;

import java.util.concurrent.TimeUnit;

/**
 * Оркестратор турнирных процессов.
 * Управляет запуском шедулеров: напоминания, отчёты, поиск новых турниров,
 * обработка завершённых, проверка старта.
 * Все задачи выполняются в общем пуле TournamentScheduler.
 */
@Component
@RequiredArgsConstructor
public class RosterOrchestrationScheduler {

    private final TournamentFinishProcessor tournamentFinishProcessor;
    private final TournamentDiscoveryService discoveryService;
    private final TournamentStartProcessor startProcessor;
    private final TournamentCanceledProcessor processCanceled;
    private final MastersApiCircuitBreaker circuitBreaker;
    private final TournamentChangeService changeService;

    // 1. Самый лёгкий — старт (не трогает API)
    @Scheduled(
            initialDelay = 30,
            fixedDelay = 10,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkStart() {
        if (circuitBreaker.isBlocked()) return;
        startProcessor.checkStart();
    }

    // 2. Лёгкий — финиш (7-9 турниров, большинство в кэше)
    @Scheduled(
            initialDelay = 2,
            fixedDelay = 12,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void processFinishedTournaments() {
        if (circuitBreaker.isBlocked()) return;
        tournamentFinishProcessor.processFinish();
    }

    // 3. Средний — новые турниры (1 запрос API, сверка внутри)
    @Scheduled(
            initialDelay = 5,
            fixedDelay = 30,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkNewTournaments() {
        if (circuitBreaker.isBlocked()) return;
        discoveryService.checkNewTournaments();
    }

    // 4. Средний — изменения (1 запрос API, сверка хэшей)
    @Scheduled(
            initialDelay = 8,
            fixedDelay = 15,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkChangedTournament() {
        if (circuitBreaker.isBlocked()) return;
        changeService.checkChangedTournaments();
    }

    // 5. Тяжёлый — отмены (53 запроса, паузы 2-3 сек)
    @Scheduled(
            initialDelay = 12,
            fixedDelay = 20,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkCanceled() {
        if (circuitBreaker.isBlocked()) return;
        processCanceled.processCanceled();
    }
}