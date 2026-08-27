package ru.pulsecore.app.admin.api.dto.request;

import java.time.LocalDate;

public record UpdateTournamentRequest(
        LocalDate date,
        String time,
        Boolean started,
        Boolean finished,
        Boolean cancelled,
        Boolean processed
) {}