// ==================== OAuthFinishRequest.java ====================
package ru.pulsecore.app.modules.auth.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthFinishRequest {
    private String lastName;
    private String firstName;
}