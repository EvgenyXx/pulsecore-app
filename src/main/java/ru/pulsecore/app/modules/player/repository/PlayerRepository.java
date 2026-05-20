package ru.pulsecore.app.modules.player.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.player.domain.Player;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {



    boolean existsByEmail(String email);

    boolean existsByNameIgnoreCase(String name);

    Optional<Player> findByEmail(String email);

    List<Player> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    List<Player> findByVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoff);

    // ── OAuth методы ──
    Optional<Player> findByOauthProviderAndOauthId(String provider, String oauthId);
    // ── конец OAuth ──
}