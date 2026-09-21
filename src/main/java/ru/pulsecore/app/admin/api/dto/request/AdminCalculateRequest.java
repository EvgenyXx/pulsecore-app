package ru.pulsecore.app.admin.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Запрос на расчёт результатов игрока за период")
public record AdminCalculateRequest(

        @Schema(
                description = "Имя игрока",
                example = "Иванов Иван",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Имя обязательно")
        String name,

        @Schema(
                description = "Дата начала (yyyy-MM-dd)",
                example = "2026-01-01",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Дата начала обязательна")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Формат: yyyy-MM-dd")
        String startDate,

        @Schema(
                description = "Дата окончания (yyyy-MM-dd)",
                example = "2026-09-21",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Дата окончания обязательна")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Формат: yyyy-MM-dd")
        String endDate
) {}