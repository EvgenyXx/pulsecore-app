package ru.pulsecore.app.shared.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AdminTournamentResponse(
        Long id,
        String link,
        LocalDate date,
        String time,
        boolean started,
        boolean finished,
        boolean cancelled,
        boolean processed,
        List<String> players
) {}