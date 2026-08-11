package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.roster.canceled.TournamentCanceledProcessor;
import ru.pulsecore.app.tournament.application.roster.discovery.TournamentDiscoveryService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentFinishProcessor;
import ru.pulsecore.app.tournament.application.roster.reminder.TournamentReminderService;
import ru.pulsecore.app.tournament.application.roster.start.TournamentStartProcessor;
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
    private final TournamentReminderService reminderService;
    private final TournamentStartProcessor startProcessor;
    private final TournamentCanceledProcessor canceledProcessor;

    @Scheduled(initialDelay = 30_000, fixedRate = 60000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void sendTournamentReminders() {
        reminderService.sendReminders();
    }

    @Scheduled(initialDelay = 10_000, fixedDelay = 900_000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkNewTournaments() {
        discoveryService.checkNewTournaments();
    }

    @Scheduled(initialDelay = 60_000, fixedRate = 420_000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void processFinishedTournaments() {
        tournamentFinishProcessor.processFinish();
    }

    @Scheduled(cron = "30 */3 * * * *", scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkStart() {
        startProcessor.checkStart();
    }


    @Scheduled(initialDelay = 120_000,fixedDelay = 600_000,scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkCanceled() {
        canceledProcessor.processCanceled();
    }
}
