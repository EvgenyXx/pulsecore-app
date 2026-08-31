package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.admin.SchedulerPauseService;
import ru.pulsecore.app.tournament.application.roster.canceled.TournamentCanceledProcessor;
import ru.pulsecore.app.tournament.application.roster.change.TournamentChangeService;
import ru.pulsecore.app.tournament.application.roster.discovery.TournamentDiscoveryService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentFinishProcessor;
import ru.pulsecore.app.tournament.application.roster.start.TournamentStartProcessor;
import ru.pulsecore.app.tournament.infrastructure.circuit.MastersApiCircuitBreaker;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RosterOrchestrationScheduler {

    private final TournamentFinishProcessor tournamentFinishProcessor;
    private final TournamentDiscoveryService discoveryService;
    private final TournamentStartProcessor startProcessor;
    private final TournamentCanceledProcessor processCanceled;
    private final MastersApiCircuitBreaker circuitBreaker;
    private final TournamentChangeService changeService;
    private final SchedulerPauseService schedulerPauseService;

    @Scheduled(
            initialDelay = 3,
            fixedDelay = 10,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkStart() {
        if (circuitBreaker.isBlocked()) return;
        startProcessor.checkStart();
    }

    @Scheduled(
            initialDelay = 2,
            fixedDelay = 12,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void processFinishedTournaments() {
        if (schedulerPauseService.isPaused()) {
            log.debug("Планировщик на паузе — processFinishedTournaments пропущен");
            return;
        }
        if (circuitBreaker.isBlocked()) return;
        tournamentFinishProcessor.processFinish();
    }

    @Scheduled(
            initialDelay = 5,
            fixedDelay = 30,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkNewTournaments() {
        if (schedulerPauseService.isPaused()) {
            log.debug("Планировщик на паузе — checkNewTournaments пропущен");
            return;
        }
        if (circuitBreaker.isBlocked()) return;
        discoveryService.checkNewTournaments();
    }

    @Scheduled(
            initialDelay = 8,
            fixedDelay = 15,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkChangedTournament() {
        if (schedulerPauseService.isPaused()) {
            log.debug("Планировщик на паузе — checkChangedTournament пропущен");
            return;
        }
        if (circuitBreaker.isBlocked()) return;
        changeService.checkChangedTournaments();
    }

    @Scheduled(
            initialDelay = 12,
            fixedDelay = 20,
            timeUnit = TimeUnit.MINUTES,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkCanceled() {
        if (schedulerPauseService.isPaused()) {
            log.debug("Планировщик на паузе — checkCanceled пропущен");
            return;
        }
        if (circuitBreaker.isBlocked()) return;
        processCanceled.processCanceled();
    }
}