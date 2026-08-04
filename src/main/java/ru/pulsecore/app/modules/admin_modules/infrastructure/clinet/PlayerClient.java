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


    MessageResponse activate(UUID playerId, int days);
    MessageResponse deactivate(UUID playerId);
    SubscriptionStatusResponse getSubscription(UUID playerId);


}
