package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.report.ScheduledReportProcessor;


@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledReportScheduler {

   private final ScheduledReportProcessor processor;

    @Scheduled(fixedDelay = 60000)
    public void sendScheduledReports() {
        processor.process();
    }
}