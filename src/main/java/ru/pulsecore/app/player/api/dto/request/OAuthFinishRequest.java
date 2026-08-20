
package ru.pulsecore.app.player.api.dto.request;

import jakarta.validation.constraints.NotBlank;



public record OAuthFinishRequest(
        @NotBlank(message = "Заполните фамилию") String lastName,
        @NotBlank(message = "Заполните имя") String firstName
) {}