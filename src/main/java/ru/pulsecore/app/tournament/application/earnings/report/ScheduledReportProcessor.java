package ru.pulsecore.app.tournament.application.earnings.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.ScheduledReportContext;
import ru.pulsecore.app.player.api.dto.response.SumResponse;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.tournament.application.earnings.sum.SumService;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.ScheduledReportProjection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledReportProcessor {

    private final ScheduledReportService reportService;
    private final MailDispatcher mailDispatcher;
    private final PlayerClient playerClient;
    private final SumService sumService;

    public void processReport() {
        List<ScheduledReportProjection> ready = reportService.findPendingBefore(LocalDateTime.now());
        if (ready.isEmpty()) return;

        // 1. Батч игроков — 1 HTTP
        Set<UUID> playerIds = ready.stream()
                .map(ScheduledReportProjection::getPlayerId)
                .collect(Collectors.toSet());

        Map<UUID, PlayerData> playerMap = playerClient.getPlayerDataByIds(playerIds).stream()
                .collect(Collectors.toMap(PlayerData::id, p -> p));

        // 2. Сбор mail-контента и id для markAsSent
        Map<UUID, MailContent> mailContent = new HashMap<>();
        List<UUID> sentIds = new ArrayList<>();

        for (ScheduledReportProjection report : ready) {
            PlayerData player = playerMap.get(report.getPlayerId());
            if (player == null) continue;

            SumResponse sum = sumService.getSum(
                    player.id(),
                    report.getDateFrom(),
                    report.getDateTo(),
                    0, Integer.MAX_VALUE);

            String period = report.getDateFrom() + " – " + report.getDateTo();

            mailContent.put(player.id(), new MailContent(
                    MailTypes.SCHEDULED_REPORT,
                    new ScheduledReportContext(
                            player.email(),
                            period,
                            String.format("%,.0f", sum.getSum()),
                            String.format("%,.0f", sum.getAverage()),
                            String.valueOf(sum.getCount() != null ? sum.getCount() : 0),
                            sum
                    )
            ));

            sentIds.add(report.getId());
        }

        // 3. Батч mail — 1 publishEvent
        mailDispatcher.send(mailContent);

        // 4. Батч UPDATE — 1 SQL
        int updated = reportService.markAsSent(sentIds);

        log.info("Отчёты: собрано={}, обновлено в БД={}", mailContent.size(), updated);
    }
}