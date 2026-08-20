package ru.pulsecore.app.player.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.pulsecore.app.player.infrastructure.validation.RussianEmail;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Email обязателен")
    @RussianEmail
    private String email;
}