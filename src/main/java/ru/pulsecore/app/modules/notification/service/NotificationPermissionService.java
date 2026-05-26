package ru.pulsecore.app.modules.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;

@Service
@RequiredArgsConstructor
public class NotificationPermissionService {

    public boolean canSendEmail(Player player) {
        return player != null && player.isNotificationsEnabled();
    }

    public boolean canSendPush(Player player) {
        return player != null && player.isPushEnabled();
    }
}