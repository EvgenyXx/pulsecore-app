package ru.pulsecore.app.modules.player.infrastructure.internal;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.shared.dto.PageViewStats;
import ru.pulsecore.app.modules.shared.dto.PlayerPageViewStats;
import ru.pulsecore.app.modules.admin.infrastructure.clinet.PlayerClient;
import ru.pulsecore.app.modules.player.application.analytic.PageViewStatsService;
import ru.pulsecore.app.modules.player.application.player.PlayerAdminService;
import ru.pulsecore.app.modules.player.application.role.RoleManagementService;
import ru.pulsecore.app.modules.player.application.subscription.SubscriptionService;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;
import ru.pulsecore.app.modules.shared.dto.PlayerData;
import ru.pulsecore.app.modules.shared.dto.SubscriptionStatusResponse;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminPlayerClientImpl implements PlayerClient {


    private final PlayerAdminService  playerAdminService;
    private final RoleManagementService roleManagementService;
    private final SubscriptionService subscriptionService;
    private final PageViewStatsService pageViewStatsService;


    @Override
    @Transactional
    public MessageResponse deletePlayer(UUID playerId) {
      return   playerAdminService.deletePlayer(playerId);
    }

    @Override
    public List<PlayerData> getPlayers() {
       return playerAdminService.getPlayers();
    }

    @Override
    @Transactional
    public MessageResponse grandRole(UUID playerId, String role) {
        roleManagementService.grantRole(playerId, role);
        return new MessageResponse("Роль " + role + " выдана");
    }

    @Override
    @Transactional
    public MessageResponse revokeRole(UUID playerId, String role) {
       roleManagementService.revokeRole(playerId, role);
        return new MessageResponse("Роль " + role + " отозвана");
    }

    @Override
    public List<String> getRoles(UUID playerId) {
       return roleManagementService.getRoleNames(playerId);
    }

    @Override
    @Transactional
    public MessageResponse activate(UUID playerId, int days) {
        subscriptionService.activate(playerId, days);
        return new MessageResponse("Подписка активирована на " + days + " дней");
    }

    @Override
    @Transactional
    public MessageResponse deactivate(UUID playerId) {
        subscriptionService.deactivate(playerId);
        return new MessageResponse("Подписка отключена");
    }

    @Override
    public SubscriptionStatusResponse getSubscription(UUID playerId) {
       var sub =  subscriptionService.getByPlayerId(playerId);
        if (sub == null) {
            return new SubscriptionStatusResponse(false, null, null);
        }
        return SubscriptionStatusResponse.builder()
                .active(sub.isActiveNow())
                .expiresAt(sub.getExpiresAt() != null ? sub.getExpiresAt().toString() : null)
                .startedAt(sub.getStartedAt() != null ? sub.getStartedAt().toString() : null)
                .build();
    }

    @Override
    public List<PlayerPageViewStats> getPlayerStats(int days) {
        return pageViewStatsService.getPlayerStats(days);
    }

    @Override
    public List<PageViewStats> getStats(int days) {
        return pageViewStatsService.getStats(days);
    }
}
