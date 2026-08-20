package ru.pulsecore.app.player.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByPlayerId(UUID playerId);

    void deleteByPlayer(Player p);

    @Query("SELECT s.player.id FROM Subscription s WHERE s.active = true AND CAST(s.expiresAt AS LocalDate) = :tomorrow")
    Set<UUID> findExpiringPlayerIds(@Param("tomorrow") LocalDate tomorrow);

    @Query("SELECT s FROM Subscription s WHERE s.active = true AND s.expiresAt < CURRENT_TIMESTAMP")
    List<Subscription> findExpired();
}