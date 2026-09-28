package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PlayerDataProjection {
    UUID getId();
    String getName();
    String getEmail();
    String getPrimaryLeague();
    boolean getPushEnabled();
    boolean getNotificationsEnabled();
    boolean getHasActiveSubscription();
    String getSelectedHalls();
    String getLiveSelectedHalls();
    LocalDateTime getLastLoginAt();
}