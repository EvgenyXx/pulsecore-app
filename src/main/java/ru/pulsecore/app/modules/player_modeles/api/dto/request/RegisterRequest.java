package ru.pulsecore.app.modules.player_modeles.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pulsecore.app.modules.player_modeles.infrastructure.validation.RussianEmail;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank @Size(min = 2, max = 100)
    private String name;

    @NotBlank  @RussianEmail
    private String email;

    @NotBlank @Size(min = 6, max = 100)
    private String password;
}