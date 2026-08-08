package ru.pulsecore.app.player.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.persistence.repository.projection.PlayerDataProjection;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    boolean existsByEmail(String email);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT p FROM Player p WHERE LOWER(p.email) = LOWER(:email)")
    Optional<Player> findByEmail(@Param("email") String email);

    List<Player> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    List<Player> findByVerifiedFalseAndCreatedAtBefore(LocalDateTime cutoff);

    Optional<Player> findByOauthProviderAndOauthId(String provider, String oauthId);


    @Query("SELECT p.id as id, p.name as name, p.email as email, p.primaryLeague" +
            " as primaryLeague, p.pushEnabled as pushEnabled, p.notificationsEnabled as " +
            "notificationsEnabled, CASE WHEN p.subscription.active = true THEN true ELSE false END as hasActiveSubscription " +
            "FROM Player p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<PlayerDataProjection> searchByName(@Param("query") String query);

    @Query("SELECT p.id as id, p.name as name, p.email as email, p.primaryLeague as " +
            "primaryLeague, p.pushEnabled as pushEnabled, p.notificationsEnabled as " +
            "notificationsEnabled, CASE WHEN p.subscription.active = true THEN true ELSE false END as hasActiveSubscription " +
            "FROM Player p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<PlayerDataProjection> findByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT p.id as id, p.name as name, p.email as email, p.primaryLeague" +
            " as primaryLeague, p.pushEnabled as pushEnabled, p.notificationsEnabled" +
            " as notificationsEnabled, CASE WHEN p.subscription.active = true THEN true ELSE false END as hasActiveSubscription " +
            "FROM Player p WHERE p.verified = true AND p.isBlocked = false")
    List<PlayerDataProjection> findByVerifiedTrueAndIsBlockedFalse();

    @Query("SELECT p.id as id, p.name as name, p.email as email, p.primaryLeague" +
            " as primaryLeague, p.pushEnabled as pushEnabled, p.notificationsEnabled as" +
            " notificationsEnabled, CASE WHEN p.subscription.active = true THEN true ELSE false END as hasActiveSubscription " +
            "FROM Player p WHERE p.id = :id")
    Optional<PlayerDataProjection> findProjectionById(@Param("id") UUID id);

    @Query("SELECT p.id AS id, p.name AS name, p.email AS email, p.primaryLeague AS primaryLeague, " +
            "p.pushEnabled AS pushEnabled, p.notificationsEnabled AS notificationsEnabled, " +
            "CASE WHEN p.subscription.active = true THEN true ELSE false END AS hasActiveSubscription " +
            "FROM Player p WHERE p.id IN :ids")
    List<PlayerDataProjection> findProjectionsByIds(@Param("ids") Set<UUID> ids);

    @Query("SELECT p.id as id, p.name as name, p.email as email, p.primaryLeague as primaryLeague, " +
            "p.pushEnabled as pushEnabled, p.notificationsEnabled as notificationsEnabled, " +
            "true as hasActiveSubscription " +
            "FROM Player p WHERE p.subscription.active = true")
    List<PlayerDataProjection> findActivePlayers();


    @Query("SELECT p.id as id, p.name as name, p.email as email, p.primaryLeague as primaryLeague, " +
            "p.pushEnabled as pushEnabled, p.notificationsEnabled as notificationsEnabled, " +
            "CASE WHEN s.active = true AND s.expiresAt > CURRENT_TIMESTAMP THEN true ELSE false END as hasActiveSubscription " +
            "FROM Player p LEFT JOIN p.subscription s")
    List<PlayerDataProjection> findAllPlayers();


}