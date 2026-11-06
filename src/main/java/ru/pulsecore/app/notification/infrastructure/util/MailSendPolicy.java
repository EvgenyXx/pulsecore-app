package ru.pulsecore.app.notification.infrastructure.util;


import lombok.experimental.UtilityClass;
import ru.pulsecore.app.notification.application.mail.MailType;
import ru.pulsecore.app.shared.dto.response.PlayerData;

@UtilityClass
public class MailSendPolicy {

     public boolean canSend(MailType type, PlayerData player) {
        if (player == null) return true;
        if (type.requiresNotificationsEnabled() && !player.notificationsEnabled()) {
            return false;
        }
        return !type.requiresActiveSubscription() || player.hasActiveSubscription();
    }
}
