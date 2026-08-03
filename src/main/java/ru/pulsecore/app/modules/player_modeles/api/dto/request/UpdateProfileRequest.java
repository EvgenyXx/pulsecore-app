package ru.pulsecore.app.modules.player_modeles.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.pulsecore.app.modules.shared.validation.RussianEmail;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Email обязателен")
    @RussianEmail
    private String email;
}