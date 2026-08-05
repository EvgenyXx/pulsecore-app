
package ru.pulsecore.app.modules.player.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateScheduledReportRequest(
        @NotNull LocalDate dateFrom,
        @NotNull LocalDate dateTo,
        @NotNull LocalDateTime scheduledAt
) {}