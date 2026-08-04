package ru.pulsecore.app.modules.admin_modules.infrastructure.clinet;

import ru.pulsecore.app.modules.shared.dto.SubscriptionStatusResponse;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;

import java.util.List;
import java.util.UUID;

public interface PlayerClient {

    MessageResponse deletePlayer(UUID playerId);

    MessageResponse grandRole(UUID playerId,String role);

    MessageResponse revokeRole(UUID playerId,String role);

    List<String> getRoles(UUID playerId);


    void activate(UUID playerId, int days);
    void deactivate(UUID playerId);
    SubscriptionStatusResponse getSubscription(UUID playerId);


}
