package ru.pulsecore.app.admin.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BroadcastRequest(

       @NotBlank(message = "Сообщение не может быть пустым")
       String message

) {
}