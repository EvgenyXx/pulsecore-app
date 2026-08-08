package ru.pulsecore.app.player.infrastructure.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.SubscriptionInfoDto;
import ru.pulsecore.app.player.application.player.PlayerService;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerRepository;

import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerDataProjection;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentPlayerClientImpl implements PlayerClient {

    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    @Override
    public List<PlayerData> getAll() {
        return playerRepository.findAllPlayers()
                .stream().map(PlayerDataProjection::toPlayerData)
                .toList();
    }

    @Override
    public List<PlayerData> getPlayerDataByIds(Set<UUID> playerIds) {
        return playerService.findPlayerByIds(playerIds);
    }

    @Override
    public SubscriptionInfoDto getSubscriptionInfo(UUID playerId) {
        var player = playerRepository.findById(playerId).orElseThrow(() ->
                new PlayerNotFoundException(String.valueOf(playerId)));
        var sub = player.getSubscription();
        if (sub != null && sub.isActiveNow()) {
            return SubscriptionInfoDto.builder()
                    .active(true)
                    .expiresAt(sub.getExpiresAt().toString())
                    .build();
        }
        return SubscriptionInfoDto.builder().active(false).build();
    }

    @Override
    public PlayerData getPlayerById(UUID playerId) {
        return playerRepository.findProjectionById(playerId)
                .map(PlayerDataProjection::toPlayerData)
                .orElseThrow(() -> new PlayerNotFoundException(playerId.toString()));
    }


    @Override
    public List<PlayerData> searchByName(String query) {
        return playerRepository.searchByName(query)
                .stream()
                .map(PlayerDataProjection::toPlayerData)
                .toList();
    }

    @Override
    public PlayerData findByName(String fullName) {
        return playerRepository.findByNameIgnoreCase(fullName)
                .map(PlayerDataProjection::toPlayerData)
                .orElseThrow(() -> new PlayerNotFoundException(fullName));
    }

    @Override
    public List<PlayerData> getAllActivePlayers() {
        return playerRepository.findActivePlayers()
                .stream()
                .map(PlayerDataProjection::toPlayerData)
                .toList();
    }
}
