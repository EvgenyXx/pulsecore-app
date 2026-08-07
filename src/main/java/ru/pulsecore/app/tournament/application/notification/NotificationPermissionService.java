package ru.pulsecore.app.tournament.application.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationPermissionService {


    private final PlayerClient playerClient;

    public Map<UUID,Boolean> canSendEmail(Set<UUID> playerIds) {

        return playerClient.getPlayerDataByIds(playerIds)
                .stream()
                .collect(Collectors.toMap(
                        PlayerData::playerId,
                        playerSettingsDto ->
                                playerSettingsDto.notificationsEnabled() && playerSettingsDto.hasActiveSubscription()
                ));
    }

    public  Map<UUID, Boolean> canSendPush(Set<UUID> playerIds) {
        return playerClient.getPlayerDataByIds(playerIds)
                .stream()
                .collect(Collectors.toMap(
                                PlayerData::playerId,
                                playerSettingsDto ->
                                        playerSettingsDto.pushEnabled() && playerSettingsDto.hasActiveSubscription()
                        )
                );
    }
}