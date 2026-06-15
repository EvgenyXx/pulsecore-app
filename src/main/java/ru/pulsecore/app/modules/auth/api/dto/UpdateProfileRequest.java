package ru.pulsecore.app.modules.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.pulsecore.app.modules.shared.validation.RussianEmail;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Email обязателен")
    @RussianEmail
    private String email;
}