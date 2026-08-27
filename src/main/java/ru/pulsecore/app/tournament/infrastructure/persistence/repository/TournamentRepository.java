package ru.pulsecore.app.tournament.infrastructure.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentAdminProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

    @Query(value = """
    SELECT 
        t.id AS id,
        t.link AS link,
        t.date AS date,
        t.time AS time,
        t.started AS started,
        t.finished AS finished,
        t.cancelled AS cancelled,
        t.processed AS processed,
        CAST(array_agg(DISTINCT p.name) AS text) AS players
    FROM tournament t
    LEFT JOIN player_notification pn ON t.id = pn.tournament_id
    LEFT JOIN players p ON p.id = pn.player_id
    WHERE t.date = CAST(:date AS DATE)
    GROUP BY t.id, t.link, t.date, t.time, t.started, t.finished, t.cancelled, t.processed
    ORDER BY t.time
""", nativeQuery = true)
    List<TournamentAdminProjection> findTournamentsWithPlayersByDate(@Param("date") LocalDate date);

    @Query(value = """
    SELECT 
        t.id AS id,
        t.link AS link,
        t.date AS date,
        t.time AS time,
        t.started AS started,
        t.finished AS finished,
        t.cancelled AS cancelled,
        t.processed AS processed,
        CAST(array_agg(DISTINCT p.name) AS text) AS players
    FROM tournament t
    LEFT JOIN player_notification pn ON t.id = pn.tournament_id
    LEFT JOIN players p ON p.id = pn.player_id
    WHERE t.id = :id
    GROUP BY t.id, t.link, t.date, t.time, t.started, t.finished, t.cancelled, t.processed
""", nativeQuery = true)
    TournamentAdminProjection findTournamentWithPlayersById(@Param("id") Long id);

    Optional<TournamentEntity> findByLink(String link);

    @Query("""
                SELECT DISTINCT t.id AS id,
                       t.externalId AS externalId,
                       t.link AS link,
                       t.date AS date,
                       t.time AS time,
                       pn.hall AS hall
                FROM TournamentEntity t
                JOIN PlayerNotification pn ON pn.tournament = t
                WHERE t.link = :link
            """)
    List<TournamentProjection> findDtoByLink(@Param("link") String link);


    @Modifying
    @Query("UPDATE TournamentEntity t SET t.time = :newTime WHERE t.id = :id")
    void updateTimeById(@Param("id") Long id, @Param("newTime") String newTime);

    @Modifying
    @Query("UPDATE TournamentEntity t SET t.date = :newDate WHERE t.id = :id")
    void updateDateById(@Param("id") Long id, @Param("newDate") LocalDate newDate);


    @Modifying
    @Query("UPDATE TournamentEntity t SET t.started = true," +
            " t.finished = true, t.cancelled = true, t.processed = true WHERE t.link = :uri")
    void markAsBroken(@Param("uri") String uri);


    @Modifying
    @Query(value = """
                DELETE FROM tournament
                WHERE id NOT IN (SELECT tournament_id FROM player_notification)
                  AND id NOT IN (SELECT tournament_id FROM tournament_results)
            """, nativeQuery = true)
    int deleteOrphans();


}