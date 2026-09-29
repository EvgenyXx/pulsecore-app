package ru.pulsecore.app.player.infrastructure.repository.projection;


import java.time.LocalDateTime;

public interface PlayerSubscriptionExpiryProjection {

    String getName();
    Boolean getActive();
    LocalDateTime getExpiresAt();
}
