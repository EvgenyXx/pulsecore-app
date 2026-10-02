package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface ScheduledReportProjection {
    UUID getId();
    UUID getPlayerId();
    LocalDate getDateFrom();
    LocalDate getDateTo();
}