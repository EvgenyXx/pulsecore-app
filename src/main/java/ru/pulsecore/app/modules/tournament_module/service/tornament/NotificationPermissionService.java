package ru.pulsecore.app.modules.tournament_module.service.tornament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.tournament_module.client.PlayerClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPermissionService {


    private final PlayerClient playerClient;

    public boolean canSendEmail(UUID playerId) {
       return playerClient.getNotificationInfo(playerId).canSendEmail();
    }

    public boolean canSendPush(UUID playerId) {
       return playerClient.getNotificationInfo(playerId).canSendPush();
    }
}