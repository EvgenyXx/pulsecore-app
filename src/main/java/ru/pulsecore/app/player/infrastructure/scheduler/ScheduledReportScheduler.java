package ru.pulsecore.app.player.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.api.dto.response.SumResponse;
import ru.pulsecore.app.player.entity.Player;
import ru.pulsecore.app.player.entity.ScheduledReport;
import ru.pulsecore.app.player.application.analytic.SumService;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.application.report.ScheduledReportService;
import ru.pulsecore.app.notification.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.ScheduledReportContext;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledReportScheduler {

    private final ScheduledReportService reportService;
    private final MailStrategyRegistry mailRegistry;
    private final SumService sumService;
    private final PlayerService playerService;

    @Scheduled(fixedDelay = 60000)
    public void sendScheduledReports() {
        List<ScheduledReport> ready = reportService.findPendingBefore(LocalDateTime.now());

        for (ScheduledReport report : ready) {
            Player player = playerService.getById(report.getPlayerId());
            SumResponse sum = sumService.getSum(
                    player.getId(),
                    report.getDateFrom(),
                    report.getDateTo(),
                    0, Integer.MAX_VALUE);

            String period = report.getDateFrom() + " – " + report.getDateTo();

            mailRegistry.send(MailTypes.SCHEDULED_REPORT,
                    new ScheduledReportContext(
                            player.getEmail(),
                            period,
                            String.format("%,.0f", sum.getSum()),
                            String.format("%,.0f", sum.getAverage()),
                            String.valueOf(sum.getCount() != null ? sum.getCount() : 0),
                            sum
                    ));

            reportService.markAsSent(report.getId());
            log.info("Отчёт {} отправлен игроку {}", report.getId(), player.getEmail());
        }
    }
}