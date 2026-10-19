package ru.pulsecore.app.tournament.api.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record SalaryH2HRequest(
        @NotNull(message = "Выберите первого игрока")
        String player1Name,

        @NotNull(message = "Выберите второго игрока")
        String player2Name,

        @NotNull(message = "Укажите дату начала периода")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate start,

        @NotNull(message = "Укажите дату окончания периода")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate end
) {}