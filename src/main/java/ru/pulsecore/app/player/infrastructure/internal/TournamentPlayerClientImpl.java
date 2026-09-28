package ru.pulsecore.app.player.infrastructure.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.SubscriptionInfoDto;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.application.subscription.SubscriptionQueryService;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.SubscriptionStatusResponse;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Реализация клиента для модуля турниров
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentPlayerClientImpl implements PlayerClient {


    private final SubscriptionQueryService subscriptionQueryService;
    private final PlayerSearchService playerSearchService;


    @Override
    public List<PlayerData> getAll() {
        return playerSearchService.getAll();
    }

    @Override
    public List<PlayerData> getPlayerDataByIds(Set<UUID> playerIds) {
        return playerSearchService.getPlayersIds(playerIds);
    }

    @Override
    public SubscriptionInfoDto getSubscriptionInfo(UUID playerId) {
        SubscriptionStatusResponse status = subscriptionQueryService.getSubscription(playerId);
        return SubscriptionInfoDto.builder()
                .active(status.activeNow())
                .expiresAt(status.expiresAt())
                .build();
    }

    @Override
    public PlayerData getPlayerById(UUID playerId) {
        return playerSearchService.getPlayerById(playerId);
    }


    @Override
    public List<PlayerData> searchByName(String query) {
        return playerSearchService.searchByName(query);
    }

    @Override
    public PlayerData findByName(String fullName) {
        return playerSearchService.findByName(fullName);
    }

    @Override
    public List<PlayerData> getAllActivePlayers() {
        return playerSearchService.getAllActivePlayers();
    }
}
