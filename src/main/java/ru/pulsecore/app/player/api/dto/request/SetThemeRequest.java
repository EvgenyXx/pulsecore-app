package ru.pulsecore.app.player.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Запрос на сохранение темы оформления")
public record SetThemeRequest(

        @Schema(
                description = "Название темы",
                example = "dark",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"dark", "ocean", "light"}
        )
        @NotBlank(message = "Тема обязательна")
        @Pattern(regexp = "^(dark|ocean|light)$", message = "Недопустимая тема")
        String theme
) {}