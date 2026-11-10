package ru.pulsecore.app.tournament.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatchEntity, UUID> {

    @Query(value = """
            SELECT 
                name AS playerName,
                COUNT(*) AS totalMatches,
                SUM(CASE WHEN winner_name = name THEN 1 ELSE 0 END) AS wins
            FROM (
                SELECT player1_name AS name, winner_name 
                FROM tournament_match 
                WHERE played_at >= :start AND played_at <= :end
            
                UNION ALL
            
                SELECT player2_name AS name, winner_name 
                FROM tournament_match 
                WHERE played_at >= :start AND played_at <= :end
            ) AS matches
            WHERE name IN (:playerNames)
            GROUP BY name
            """, nativeQuery = true)
    List<PlayerMatchStatsProjection> getMatchStatsByPlayerNames(
            @Param("playerNames") List<String> playerNames,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT m.score AS score, m.winnerName AS winnerName
                FROM TournamentMatchEntity m
                WHERE 
                    ((m.player1Name = :player1Name AND m.player2Name = :player2Name)
                    OR (m.player1Name = :player2Name AND m.player2Name = :player1Name))
                    AND m.playedAt BETWEEN :start AND :end
                    AND m.score IS NOT NULL
            """)
    List<MatchScoreProjection> findMatchScores(
            @Param("player1Name") String player1Name,
            @Param("player2Name") String player2Name,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
            SELECT 
                p.name::text AS playerName,
                COUNT(tm.id) AS totalMatches,
                SUM(CASE WHEN tm.winner_name = LOWER(p.name::text) THEN 1 ELSE 0 END) AS wins
            FROM players p
            LEFT JOIN tournament_match tm ON (tm.player1_name = LOWER(p.name::text) OR tm.player2_name = LOWER(p.name::text))
                AND tm.played_at >= :start AND tm.played_at <= :end
            WHERE LOWER(p.name::text) IN (:playerNames)
            GROUP BY p.name::text
            """, nativeQuery = true)
    List<PlayerMatchStatsProjection> findWinrateForCompare(
            @Param("playerNames") List<String> playerNames,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT
                    m.playedAt AS playedAt,
                    m.score AS score,
                    m.winnerName AS winnerName
                FROM TournamentMatchEntity m
                WHERE
                    ((m.player1Name = :player1Name AND m.player2Name = :player2Name)
                    OR (m.player1Name = :player2Name AND m.player2Name = :player1Name))
                    AND m.playedAt BETWEEN :start AND :end
                ORDER BY m.playedAt DESC
            """)
    Page<PlayerH2HMatchProjection> findH2HMatches(
            @Param("player1Name") String player1Name,
            @Param("player2Name") String player2Name,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("""
                SELECT m.stage AS stage,
                       m.player1Name AS player1Name,
                       m.player2Name AS player2Name,
                       m.score AS score,
                       m.winnerName AS winnerName,
                       m.playedAt AS playedAt
                FROM TournamentMatchEntity m
                WHERE m.tournament.id = :tournamentId
                ORDER BY 
                    CASE m.stage 
                        WHEN 'GROUP' THEN 1 
                        WHEN 'SEMIFINAL' THEN 2 
                        WHEN 'THIRD_PLACE' THEN 3 
                        WHEN 'FINAL' THEN 4 
                    END
            """)
    List<TournamentMatchProjection> findMatchesByTournamentId(@Param("tournamentId") Long tournamentId);

    boolean existsByTournamentId(Long tournamentId);

    @Query("""
                SELECT
                    COUNT(*) AS totalMatches,
                    SUM(CASE WHEN m.winnerName = :player1Name THEN 1 ELSE 0 END) AS player1Wins,
                    SUM(CASE WHEN m.winnerName = :player2Name THEN 1 ELSE 0 END) AS player2Wins
                FROM TournamentMatchEntity m
                WHERE
                    ((m.player1Name = :player1Name AND m.player2Name = :player2Name)
                    OR (m.player1Name = :player2Name AND m.player2Name = :player1Name))
                    AND m.playedAt BETWEEN :start AND :end
            """)
    PlayerH2HSummaryProjection findH2HSummary(
            @Param("player1Name") String player1Name,
            @Param("player2Name") String player2Name,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(
            value = """
                        SELECT DISTINCT name FROM (
                            SELECT player1_name AS name FROM tournament_match
                            UNION
                            SELECT player2_name AS name FROM tournament_match
                        ) AS all_players
                        WHERE name LIKE CONCAT('%', :query, '%')
                        ORDER BY name
                    """,
            countQuery = """
                        SELECT COUNT(DISTINCT name) FROM (
                            SELECT player1_name AS name FROM tournament_match
                            UNION
                            SELECT player2_name AS name FROM tournament_match
                        ) AS all_players
                        WHERE name LIKE CONCAT('%', :query, '%')
                    """,
            nativeQuery = true
    )
    Page<String> searchPlayerNames(@Param("query") String query, Pageable pageable);



    @Query("SELECT DISTINCT m.tournament.id FROM TournamentMatchEntity m WHERE m.tournament.id IN :tournamentIds")
    Set<Long> findExistingTournamentIds(@Param("tournamentIds") List<Long> tournamentIds);
}