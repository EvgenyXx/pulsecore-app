package ru.pulsecore.app.modules.push;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    List<PushSubscription> findByPlayerId(UUID playerId);

    Optional<PushSubscription> findByPlayerIdAndEndpoint(UUID playerId, String endpoint);

    void deleteByEndpoint(String endpoint);

    List<PushSubscription> findAllByPlayerIdIn(List<UUID> playerIds);
}