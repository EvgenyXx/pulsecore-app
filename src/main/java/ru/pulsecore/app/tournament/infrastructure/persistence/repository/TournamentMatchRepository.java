package ru.pulsecore.app.tournament.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
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


}