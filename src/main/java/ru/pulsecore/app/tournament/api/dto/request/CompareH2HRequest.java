package ru.pulsecore.app.tournament.api.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import ru.pulsecore.app.tournament.domain.enums.ComparePeriod;

import java.time.LocalDate;

public record CompareH2HRequest(
        @NotNull(message = "Выберите первого игрока")
        String player1Name,

        @NotNull(message = "Выберите второго игрока")
        String player2Name,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate start,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate end,


        String period
) {
    public LocalDate resolveStart() {
        if (start != null) {
            return start;
        }
        return getActualPeriod().startDate();
    }

    public LocalDate resolveEnd() {
        if (end != null) {
            return end;
        }
        return getActualPeriod().endDate();
    }


    private ComparePeriod getActualPeriod() {
        if (period == null || period.isBlank()) {
            return ComparePeriod.MONTH;
        }
        return ComparePeriod.fromCode(period);
    }
}