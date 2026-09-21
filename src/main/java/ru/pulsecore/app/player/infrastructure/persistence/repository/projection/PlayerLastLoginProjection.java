package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;

import java.time.LocalDateTime;

public interface PlayerLastLoginProjection {

    String getName();

    LocalDateTime getLastLoginAt();
}
