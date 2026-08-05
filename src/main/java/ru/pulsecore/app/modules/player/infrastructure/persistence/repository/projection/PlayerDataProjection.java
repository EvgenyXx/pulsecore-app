package ru.pulsecore.app.modules.player.infrastructure.persistence.repository.projection;

import java.util.UUID;

public interface PlayerDataProjection {
    UUID getId();
    String getName();
    String getEmail();
}