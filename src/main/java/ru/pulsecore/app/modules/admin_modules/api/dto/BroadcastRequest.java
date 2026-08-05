package ru.pulsecore.app.modules.admin_modules.api.dto;

import jakarta.validation.constraints.NotBlank;

public record BroadcastRequest(

       @NotBlank(message = "Сообщение не может быть пустым")
       String message

) {
}