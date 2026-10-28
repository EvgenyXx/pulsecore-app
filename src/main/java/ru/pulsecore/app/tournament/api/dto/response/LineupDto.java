package ru.pulsecore.app.tournament.api.dto.response;

import java.time.LocalDate;

public record LineupDto(
    String time,
    String league,
    String hall,
    String players,
    LocalDate date,
    boolean isPlayer,
    String link,
    String type
) {}