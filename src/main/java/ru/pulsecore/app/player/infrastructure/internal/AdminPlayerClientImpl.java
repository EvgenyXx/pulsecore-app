package ru.pulsecore.app.player.infrastructure.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.api.dto.request.UpdatePlayerRequest;
import ru.pulsecore.app.player.application.admin.PlayerUpdateAdminService;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.application.subscription.SubscriptionQueryService;
import ru.pulsecore.app.shared.dto.response.*;
import ru.pulsecore.app.admin.client.PlayerClient;
import ru.pulsecore.app.player.application.analytic.PageViewStatsService;
import ru.pulsecore.app.player.application.admin.PlayerAdminService;
import ru.pulsecore.app.player.application.role.RoleManagementService;
import ru.pulsecore.app.player.application.subscription.SubscriptionCommandService;
import java.util.List;
import java.util.UUID;


/**
 * Клиент для админ модуля
 */
@Service
@RequiredArgsConstructor
public class AdminPlayerClientImpl implements PlayerClient {


    private final PlayerAdminService playerAdminService;
    private final RoleManagementService roleManagementService;
    private final SubscriptionCommandService subscriptionCommandService;
    private final PageViewStatsService pageViewStatsService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final PlayerUpdateAdminService updateAdminService;
    private final PlayerSearchService playerSearchService;


    @Override
    public List<PlayerSubscriptionResponse> getSubscription() {
       return playerAdminService.getSubscription();
    }

    @Override
    public Page<LastLoginResponse> getLastLogin(Pageable pageable) {
       return playerAdminService.getLastLogin(pageable);
    }

    @Override
    public PlayerData updatePlayer(UUID playerId, UpdatePlayerRequest request) {
        return updateAdminService.updatePlayer(playerId, request);
    }

    @Override
    public Page<PlayerData> searchByNamePage(String name, int page, int size) {
        return playerSearchService.searchByNamePage(name, page, size);
    }

    @Override
    public MessageResponse deletePlayer(UUID playerId) {
        return playerAdminService.deletePlayer(playerId);
    }

    @Override
    public List<PlayerData> getPlayers() {
        return playerAdminService.getPlayers();
    }

    @Override
    public MessageResponse grandRole(UUID playerId, String role) {
        roleManagementService.grantRole(playerId, role);
        return new MessageResponse("Роль " + role + " выдана");
    }

    @Override
    public MessageResponse revokeRole(UUID playerId, String role) {
        roleManagementService.revokeRole(playerId, role);
        return new MessageResponse("Роль " + role + " отозвана");
    }

    @Override
    public List<String> getRoles(UUID playerId) {
        return roleManagementService.getRoleNames(playerId);
    }

    @Override
    public MessageResponse activate(UUID playerId, int days) {
        subscriptionCommandService.activate(playerId, days);
        return new MessageResponse("Подписка активирована на " + days + " дней");
    }

    @Override
    public MessageResponse deactivate(UUID playerId) {
        subscriptionCommandService.deactivate(playerId);
        return new MessageResponse("Подписка отключена");
    }

    @Override
    public MessageResponse enable(UUID playerId) {
        subscriptionCommandService.enable(playerId);
        return new MessageResponse("Подписка включена");
    }

    @Override
    public SubscriptionStatusResponse getSubscription(UUID playerId) {
        return subscriptionQueryService.getSubscription(playerId);
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
