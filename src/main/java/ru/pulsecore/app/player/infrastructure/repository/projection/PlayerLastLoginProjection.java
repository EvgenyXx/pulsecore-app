package ru.pulsecore.app.player.infrastructure.repository.projection;

import java.time.LocalDateTime;

public interface PlayerLastLoginProjection {

    String getName();

    LocalDateTime getLastLoginAt();
}
