package ru.pulsecore.app.player.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.player.infrastructure.persistence.entity.Player;
import ru.pulsecore.app.player.infrastructure.persistence.entity.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByPlayerId(UUID playerId);

    void deleteByPlayer(Player p);

    @Query(value = "SELECT * FROM subscription WHERE active = true AND expires_at::date = CURRENT_DATE + 1", nativeQuery = true)
    List<Subscription> findExpiringTomorrow();

    @Query("SELECT s FROM Subscription s WHERE s.active = true AND s.expiresAt < CURRENT_TIMESTAMP")
    List<Subscription> findExpired();
}