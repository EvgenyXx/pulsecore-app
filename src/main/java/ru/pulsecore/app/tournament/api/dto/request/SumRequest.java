package ru.pulsecore.app.tournament.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "Параметры запроса суммы за период")
public record SumRequest(

        @Schema(description = "Дата начала", example = "2026-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate start,

        @Schema(description = "Дата окончания", example = "2026-09-21")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate end
) {
    @AssertTrue(message = "Укажите start или end")
    @Schema(hidden = true)
    public boolean isDateRangeValid() {
        return start != null || end != null;
    }
}