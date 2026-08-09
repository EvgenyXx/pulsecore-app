package ru.pulsecore.app.tournament.infrastructure.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.discovery.TournamentDiscoveryService;
import ru.pulsecore.app.tournament.application.finish.TournamentFinishProcessor;
import ru.pulsecore.app.tournament.application.reminder.TournamentReminderService;
import ru.pulsecore.app.tournament.application.report.ScheduledReportProcessor;
import ru.pulsecore.app.tournament.application.start.TournamentStartProcessor;

@Component
@RequiredArgsConstructor
public class TournamentScheduler {


    private final TournamentFinishProcessor tournamentFinishProcessor;
    private final TournamentDiscoveryService discoveryService;
    private final TournamentReminderService reminderService;
    private final ScheduledReportProcessor processor;
    private final TournamentStartProcessor startProcessor;

    @Scheduled(fixedRate = 60000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void sendTournamentReminders() {
        reminderService.sendReminders();
    }

    @Scheduled(fixedDelay = 60000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void sendScheduledReports() {
        processor.processReport();
    }

    @Scheduled(fixedDelay = 900_000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkNewTournaments() {
        discoveryService.checkNewTournaments();
    }

    @Scheduled(fixedRate = 420_000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void processFinishedTournaments() {
        tournamentFinishProcessor.processFinish();
    }

    @Scheduled(cron = "0 */3 * * * *", scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void checkStart() {
        startProcessor.checkStart();
    }
}
