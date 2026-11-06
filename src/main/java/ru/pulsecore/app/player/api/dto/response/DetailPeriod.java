package ru.pulsecore.app.player.api.dto.response;

import java.io.Serializable;

public record DetailPeriod(
        Double totalAmount,
        Double averageAmount,
        Long tournaments,
        Long matchesCount,
        Long wins,
        Long loses
) implements Serializable {}