package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.config.SchedulerConfig;
import ru.pulsecore.app.tournament.application.earnings.report.ScheduledReportProcessor;

/**
 * Шедулер отправки отчётов о заработке на почту.
 * Запускает ScheduledReportProcessor для обработки запланированных отчётов.
 */
@Component
@RequiredArgsConstructor
public class EarningsReportScheduler {


    private final ScheduledReportProcessor processor;

    @Scheduled(initialDelay = 90_000, fixedDelay = 60000, scheduler = SchedulerConfig.TOURNAMENT_SCHEDULER)
    public void sendScheduledReports() {
        processor.processReport();
    }
}
