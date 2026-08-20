package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.roster.reminder.MoscowReminderService;
import ru.pulsecore.app.tournament.application.roster.reminder.OrenburgReminderService;
import ru.pulsecore.app.tournament.application.roster.reminder.VladivostokReminderService;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final MoscowReminderService moscowReminderService;
    private final VladivostokReminderService vladivostokReminderService;
    private final OrenburgReminderService orenburgReminderService;

    @Scheduled(
            initialDelay = 0,
            fixedRate = 60,
            timeUnit = TimeUnit.SECONDS,
            scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void sendTournamentReminders() {
        CompletableFuture<Void> moscow = CompletableFuture.runAsync(moscowReminderService::sendReminders);
        CompletableFuture<Void> vladivostok = CompletableFuture.runAsync(vladivostokReminderService::sendReminders);
        CompletableFuture<Void> orenburg = CompletableFuture.runAsync(orenburgReminderService::sendReminders);

        CompletableFuture.allOf(moscow, vladivostok, orenburg).join();

        log.debug("Региональные напоминания запущены параллельно");
    }
}