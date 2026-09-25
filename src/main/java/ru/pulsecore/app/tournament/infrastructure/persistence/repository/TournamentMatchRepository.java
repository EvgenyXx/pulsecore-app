package ru.pulsecore.app.tournament.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerH2HProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerH2HSummaryProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerMatchStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentMatchProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TournamentMatchRepository extends JpaRepository<TournamentMatchEntity, UUID> {

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

    /**
     * Идемпотентная вставка матча.
     * При дубликате по (tournament_id, player1_name, player2_name, stage) —
     * строка НЕ вставляется, исключение НЕ бросается.
     */
    @Modifying
    @Query(value = """
            INSERT INTO tournament_match
                (id, tournament_id, player1_name, player2_name, winner_name, stage, score, created_at, played_at)
            VALUES
                (:id, :tournamentId, :player1Name, :player2Name, :winnerName, :stage, :score, :createdAt, :playedAt)
            ON CONFLICT (tournament_id, player1_name, player2_name, stage) DO NOTHING
            """, nativeQuery = true)
    void insertIfNotExists(
            @Param("id") UUID id,
            @Param("tournamentId") Long tournamentId,
            @Param("player1Name") String player1Name,
            @Param("player2Name") String player2Name,
            @Param("winnerName") String winnerName,
            @Param("stage") String stage,
            @Param("score") String score,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("playedAt") LocalDateTime playedAt
    );

    @Query("""
                SELECT 
                    p.name AS playerName,
                    ROUND(100.0 * SUM(CASE WHEN m.stage = 'GROUP' AND LOWER(m.winnerName) = LOWER(p.name) THEN 1 ELSE 0 END) / NULLIF(COUNT(CASE WHEN m.stage = 'GROUP' THEN 1 END), 0), 1) AS groupWinPercent,
                    ROUND(100.0 * SUM(CASE WHEN m.stage = 'SEMIFINAL' AND LOWER(m.winnerName) = LOWER(p.name) THEN 1 ELSE 0 END) / NULLIF(COUNT(CASE WHEN m.stage = 'SEMIFINAL' THEN 1 END), 0), 1) AS semifinalWinPercent,
                    ROUND(100.0 * SUM(CASE WHEN m.stage = 'THIRD_PLACE' AND LOWER(m.winnerName) = LOWER(p.name) THEN 1 ELSE 0 END) / NULLIF(COUNT(CASE WHEN m.stage = 'THIRD_PLACE' THEN 1 END), 0), 1) AS thirdPlaceWinPercent,
                    ROUND(100.0 * SUM(CASE WHEN m.stage = 'FINAL' AND LOWER(m.winnerName) = LOWER(p.name) THEN 1 ELSE 0 END) / NULLIF(COUNT(CASE WHEN m.stage = 'FINAL' THEN 1 END), 0), 1) AS finalWinPercent
                FROM Player p
                LEFT JOIN TournamentMatchEntity m ON LOWER(m.player1Name) = LOWER(p.name) OR LOWER(m.player2Name) = LOWER(p.name)
                WHERE m.playedAt BETWEEN :start AND :end
                GROUP BY p.name
                ORDER BY LOWER(p.name) ASC
            """)
    List<PlayerMatchStatsProjection> findPlayersMatchStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

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

    @Query("""
                SELECT 
                    m.stage AS stage,
                    COUNT(*) AS totalMatches,
                    SUM(CASE WHEN LOWER(m.winnerName) = LOWER(:player1Name) THEN 1 ELSE 0 END) AS player1Wins,
                    SUM(CASE WHEN LOWER(m.winnerName) = LOWER(:player2Name) THEN 1 ELSE 0 END) AS player2Wins
                FROM TournamentMatchEntity m
                WHERE 
                    ((LOWER(m.player1Name) = LOWER(:player1Name) AND LOWER(m.player2Name) = LOWER(:player2Name))
                    OR (LOWER(m.player1Name) = LOWER(:player2Name) AND LOWER(m.player2Name) = LOWER(:player1Name)))
                    AND m.playedAt BETWEEN :start AND :end
                GROUP BY m.stage
                ORDER BY 
                    CASE m.stage 
                        WHEN 'GROUP' THEN 1 
                        WHEN 'SEMIFINAL' THEN 2 
                        WHEN 'THIRD_PLACE' THEN 3 
                        WHEN 'FINAL' THEN 4 
                    END
            """)
    List<PlayerH2HProjection> findH2HByStage(
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