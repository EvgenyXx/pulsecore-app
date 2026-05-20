package ru.pulsecore.app.modules.player.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.domain.Subscription;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByPlayerId(UUID playerId);

    void deleteByPlayer(Player p);
}