package ru.pulsecore.app.tournament.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PlayerNotificationRepository
        extends JpaRepository<PlayerNotification, Long> {

    @Modifying
    @Query("UPDATE PlayerNotification pn SET pn.hall = :newHall WHERE pn.tournament.id = :tournamentId")
    void updateHallByTournamentId(@Param("tournamentId") Long tournamentId, @Param("newHall") Integer newHall);

    @Modifying
    @Query("DELETE FROM PlayerNotification p WHERE p.playerId = :playerId")
    int deleteByPlayerId(@Param("playerId") UUID playerId);

    @Modifying
    @Query("DELETE FROM PlayerNotification pn WHERE pn.playerId = :playerId AND pn.tournament.id = :tournamentId")
    void deleteByPlayerIdAndTournamentId(@Param("playerId") UUID playerId, @Param("tournamentId") Long tournamentId);

    boolean existsByPlayerIdAndTournament_ExternalId(UUID playerId, Long externalId);

    void deleteByTournament_FinishedTrueAndTournament_DateBefore(LocalDate date);

    @Query("""
                SELECT pn
                FROM PlayerNotification pn
                JOIN pn.tournament t
                WHERE t.finished = false
                  AND t.cancelled = false
                  AND pn.pushReminderSent = false
            """)
    List<PlayerNotification> findPendingWithTournament();


    @Query("SELECT DISTINCT t.link FROM PlayerNotification pn JOIN pn.tournament t" +
            " WHERE t.started = false AND t.finished = false AND t.cancelled = false AND" +
            " t.date = CURRENT_DATE AND t.time <= :currentTime")
    List<String> findStartingSoonLinks(@Param("currentTime") String currentTime);


    @Query(value = """
                 select distinct
                     t.link
                     from player_notification p
                 left join tournament t on p.tournament_id = t.id
                 where t.started = true
                 and  t.processed=false
                 and t.finished = false
                 and t.cancelled= false
                 and t.date <= current_date
            """, nativeQuery = true)
    List<String> findStartedNotFinishedLinks();


    @Query("SELECT pn FROM PlayerNotification pn JOIN FETCH pn.tournament WHERE pn.tournament.link = :link")
    List<PlayerNotification> findByTournamentLink(@Param("link") String link);


    @Query("SELECT DISTINCT t.link FROM PlayerNotification pn" +
            " JOIN pn.tournament t WHERE t.started = false AND " +
            "t.cancelled = false AND t.finished = false")
    List<String> findNotStartedForCancelLinks();


    Optional<PlayerNotification> findByPlayerIdAndTournamentId(UUID playerId, Long tournamentId);

    @Query("SELECT pn.playerId FROM PlayerNotification pn WHERE pn.tournament.id = :tournamentId")
    Set<UUID> findPlayerIdsByTournamentId(@Param("tournamentId") Long tournamentId);


    @Query("SELECT DISTINCT pn.hall FROM PlayerNotification pn WHERE pn.tournament.id = :tournamentId")
    List<Integer> findHallsByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("""
                SELECT pn
                FROM PlayerNotification pn
                JOIN pn.tournament t
                WHERE pn.hall IN :halls
                  AND t.finished = false
                  AND t.cancelled = false
                  AND (pn.pushReminderSent = false OR pn.pushEveningSent = false)
            """)
    List<PlayerNotification> findPendingByHalls(@Param("halls") List<Integer> halls);
}