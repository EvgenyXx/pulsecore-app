package ru.pulsecore.app.modules.admin.infrastructure.clinet;

import ru.pulsecore.app.modules.shared.dto.PageViewStats;
import ru.pulsecore.app.modules.shared.dto.PlayerPageViewStats;
import ru.pulsecore.app.modules.shared.dto.PlayerData;
import ru.pulsecore.app.modules.shared.dto.SubscriptionStatusResponse;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface PlayerClient {

    //player
    MessageResponse deletePlayer(UUID playerId);
    List<PlayerData> getPlayers();

    //analytic
    List<PlayerPageViewStats> getPlayerStats(int days);
    List<PageViewStats> getStats(int days);

    //role
    MessageResponse grandRole(UUID playerId,String role);
    MessageResponse revokeRole(UUID playerId,String role);
    List<String> getRoles(UUID playerId);

    //subscription
    MessageResponse activate(UUID playerId, int days);
    MessageResponse deactivate(UUID playerId);
    SubscriptionStatusResponse getSubscription(UUID playerId);


}
