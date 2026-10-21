package ru.pulsecore.app.tournament.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatchEntity, UUID> {

    @Query("""
                SELECT m.score AS score, m.winnerName AS winnerName
                FROM TournamentMatchEntity m
                WHERE 
                    ((LOWER(m.player1Name) = LOWER(:player1Name) AND LOWER(m.player2Name) = LOWER(:player2Name))
                    OR (LOWER(m.player1Name) = LOWER(:player2Name) AND LOWER(m.player2Name) = LOWER(:player1Name)))
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
                SUM(CASE WHEN LOWER(tm.winner_name) = LOWER(p.name::text) THEN 1 ELSE 0 END) AS wins
            FROM players p
            LEFT JOIN tournament_match tm ON (LOWER(tm.player1_name) = LOWER(p.name::text) OR LOWER(tm.player2_name) = LOWER(p.name::text))
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
                    ((LOWER(m.player1Name) = LOWER(:player1Name) AND LOWER(m.player2Name) = LOWER(:player2Name))
                    OR (LOWER(m.player1Name) = LOWER(:player2Name) AND LOWER(m.player2Name) = LOWER(:player1Name)))
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
                    SUM(CASE WHEN LOWER(m.winnerName) = LOWER(:player1Name) THEN 1 ELSE 0 END) AS player1Wins,
                    SUM(CASE WHEN LOWER(m.winnerName) = LOWER(:player2Name) THEN 1 ELSE 0 END) AS player2Wins
                FROM TournamentMatchEntity m
                WHERE
                    ((LOWER(m.player1Name) = LOWER(:player1Name) AND LOWER(m.player2Name) = LOWER(:player2Name))
                    OR (LOWER(m.player1Name) = LOWER(:player2Name) AND LOWER(m.player2Name) = LOWER(:player1Name)))
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
                        WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%'))
                        ORDER BY name
                    """,
            countQuery = """
                        SELECT COUNT(DISTINCT name) FROM (
                            SELECT player1_name AS name FROM tournament_match
                            UNION
                            SELECT player2_name AS name FROM tournament_match
                        ) AS all_players
                        WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%'))
                    """,
            nativeQuery = true
    )
    Page<String> searchPlayerNames(@Param("query") String query, Pageable pageable);
}