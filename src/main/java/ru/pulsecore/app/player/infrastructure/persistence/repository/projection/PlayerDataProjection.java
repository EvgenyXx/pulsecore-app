package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;

import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.UUID;

public interface PlayerDataProjection {
    UUID getId();
    String getName();
    String getEmail();
    String getPrimaryLeague();
    boolean getPushEnabled();
    boolean getNotificationsEnabled();
    Boolean getHasActiveSubscription();

    default PlayerData toPlayerData() {
        return new PlayerData(
                getId(),
                getName(),
                getEmail(),
                getPrimaryLeague(),
                getPushEnabled(),
                getNotificationsEnabled(),
                getHasActiveSubscription() != null && getHasActiveSubscription()
        );
    }
}