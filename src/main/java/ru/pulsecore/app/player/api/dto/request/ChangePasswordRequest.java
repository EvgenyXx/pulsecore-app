package ru.pulsecore.app.player.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Введите текущий пароль")
    private String oldPassword;

    @NotBlank(message = "Введите новый пароль")
    @Size(min = 6, message = "Новый пароль должен содержать не менее 6 символов")
    private String newPassword;
}