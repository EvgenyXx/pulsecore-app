// ==================== OAuthFinishRequest.java ====================
package ru.pulsecore.app.modules.player_modeles.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthFinishRequest {
    private String lastName;
    private String firstName;
}