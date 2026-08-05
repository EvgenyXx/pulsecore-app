// NotificationsStatusResponse.java
package ru.pulsecore.app.player.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsStatusResponse {
    private boolean enabled;
}