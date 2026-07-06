package ru.pulsecore.app.modules.player.api.dto.dashboard;

import ru.pulsecore.app.modules.player.domain.ScheduledReport;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduledReportResponse(
        UUID id,
        LocalDate dateFrom,
        LocalDate dateTo,
        LocalDateTime scheduledAt,
        String status
) implements Serializable {

    public static ScheduledReportResponse from(ScheduledReport report) {
        return new ScheduledReportResponse(
                report.getId(),
                report.getDateFrom(),
                report.getDateTo(),
                report.getScheduledAt(),
                report.getStatus().name()
        );
    }
}