package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;


import java.time.LocalDateTime;

public interface PlayerSubscriptionExpiryProjection {

    String getName();
    Boolean getActive();
    LocalDateTime getExpiresAt();
}
