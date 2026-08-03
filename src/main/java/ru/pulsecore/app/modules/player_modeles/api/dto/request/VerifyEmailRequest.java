package ru.pulsecore.app.modules.player_modeles.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String code;
}