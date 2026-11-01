package ru.pulsecore.app.shared.api.player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.pulsecore.app.admin.api.dto.request.UpdatePlayerRequest;
import ru.pulsecore.app.shared.dto.response.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PlayerAdminClient {

    //player
    MessageResponse deletePlayer(UUID playerId);
    List<PlayerData> getPlayers();
    Page<PlayerData> searchByNamePage(String name, int page, int size);
    PlayerData updatePlayer(UUID playerId, UpdatePlayerRequest request);
    Page<LastLoginResponse> getLastLogin(Pageable pageable);
    List<PlayerSubscriptionResponse>getSubscription();

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
    MessageResponse enable(UUID playerId);
    SubscriptionStatusResponse getSubscription(UUID playerId);
    MessageResponse activateSubscriptionForPeriod(UUID playerId,LocalDateTime start,LocalDateTime end);


}
