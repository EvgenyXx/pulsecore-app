package ru.pulsecore.app.tournament.infrastructure.cache.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.YearMonth;

public record SyncState(
        String playerName,
        YearMonth currentMonth,
        YearMonth endMonth,
        Instant createdAt
) implements Serializable {}