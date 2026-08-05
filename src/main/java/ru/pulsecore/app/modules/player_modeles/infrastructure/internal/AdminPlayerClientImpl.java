package ru.pulsecore.app.modules.player_modeles.infrastructure.internal;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.admin_modules.infrastructure.clinet.PlayerClient;
import ru.pulsecore.app.modules.player_modeles.application.role.RoleManagementService;
import ru.pulsecore.app.modules.player_modeles.application.subscription.SubscriptionService;
import ru.pulsecore.app.modules.player_modeles.infrastructure.client.TournamentClient;
import ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository.PlayerRepository;
import ru.pulsecore.app.modules.player_modeles.infrastructure.session.SessionService;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;
import ru.pulsecore.app.modules.shared.dto.PlayerData;
import ru.pulsecore.app.modules.shared.dto.SubscriptionStatusResponse;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminPlayerClientImpl implements PlayerClient {

    private final PlayerRepository playerRepository;
    private final TournamentClient tournamentClient;
    private final RedisIndexedSessionRepository sessionRepository;
    private final SessionService sessionService;
    private final RoleManagementService roleManagementService;
    private final SubscriptionService subscriptionService;


    @Override
    @Transactional
    public MessageResponse deletePlayer(UUID playerId) {
       tournamentClient.deleteByPlayerId(playerId);
        String principalName = playerId.toString();
        sessionRepository.findByPrincipalName(principalName)
                .forEach((sessionId, session) -> sessionRepository.deleteById(sessionId));
        playerRepository.deleteById(playerId);
        sessionService.invalidateCurrentSession();
        return new MessageResponse("Аккаунт удалён");
    }

    @Override
    public List<PlayerData> getPlayers() {
       return playerRepository.findByVerifiedTrueAndIsBlockedFalse()
               .stream()
               .map(p->
                       new PlayerData(p.getId(),p.getName(),p.getEmail()))
               .toList();
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
}
