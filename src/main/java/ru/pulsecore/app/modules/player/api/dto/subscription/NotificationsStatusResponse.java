// NotificationsStatusResponse.java
package ru.pulsecore.app.modules.player.api.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsStatusResponse {
    private boolean enabled;
}