package ru.pulsecore.app.player.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyPasswordRequest {
    @NotBlank
    private String password;
}