package ru.pulsecore.app.admin.api.dto.request;

import java.time.LocalDateTime;

public record ActivatePeriodRequest(
        LocalDateTime start,
        LocalDateTime end
) {}