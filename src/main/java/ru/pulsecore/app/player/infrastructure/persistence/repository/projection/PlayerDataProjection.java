package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;

import java.util.UUID;

public interface PlayerDataProjection {
    UUID getId();
    String getName();
    String getEmail();
    String getPrimaryLeague();
    boolean getPushEnabled();
    boolean getNotificationsEnabled();
}