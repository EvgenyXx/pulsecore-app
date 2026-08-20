package ru.pulsecore.app.player.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String code;
}