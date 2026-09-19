package ru.pulsecore.app.admin.api.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Запрос на ресинхронизацию турниров игрока за период.
 */
public record ResyncRequest(

        @NotNull(message = "from обязателен")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @NotNull(message = "to обязателен")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to
) {
}