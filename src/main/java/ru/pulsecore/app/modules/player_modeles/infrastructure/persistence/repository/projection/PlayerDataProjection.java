package ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository.projection;

import java.util.UUID;

public interface PlayerDataProjection {
    UUID getId();
    String getName();
    String getEmail();
}