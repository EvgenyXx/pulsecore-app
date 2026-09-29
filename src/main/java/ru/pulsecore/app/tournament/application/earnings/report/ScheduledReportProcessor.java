package ru.pulsecore.app.tournament.application.earnings.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.ScheduledReportContext;
import ru.pulsecore.app.player.api.dto.response.SumResponse;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.tournament.application.earnings.sum.SumService;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.ScheduledReportProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledReportProcessor {
    private final ScheduledReportService reportService;
    private final ApplicationEventPublisher eventPublisher;
    private final PlayerClient playerClient;
    private final SumService sumService;


    public void processReport() {
        List<ScheduledReportProjection> ready = reportService.findPendingBefore(LocalDateTime.now());

        for (var report : ready) {
            PlayerData player = playerClient.getPlayerById(report.getPlayerId());
            SumResponse sum = sumService.getSum(
                    player.id(),
                    report.getDateFrom(),
                    report.getDateTo(),
                    0, Integer.MAX_VALUE);

            String period = report.getDateFrom() + " – " + report.getDateTo();

            publishEvent(player.email(),player.id(), period, sum);

            reportService.markAsSent(report.getId());
            log.info("Отчёт отправлен игроку {}. Почта: {}", player.name(),player.email());
        }
    }

    private void publishEvent(String email, UUID playerId, String period, SumResponse sum) {
        eventPublisher.publishEvent(
                new MailNotificationEvent(
                        MailTypes.SCHEDULED_REPORT,
                        playerId,
                        new ScheduledReportContext(
                                email,
                                period,
                                String.format("%,.0f", sum.getSum()),
                                String.format("%,.0f", sum.getAverage()),
                                String.valueOf(sum.getCount() != null ? sum.getCount() : 0),
                                sum
                        )
                )
        );
    }
}