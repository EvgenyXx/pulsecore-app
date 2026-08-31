
package ru.pulsecore.app.tournament.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.*;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TournamentResultRepository extends JpaRepository<TournamentResultEntity, Long> {




    @Query(value = """
    SELECT player_id AS playerId, league
    FROM (
        SELECT player_id, league, ROW_NUMBER() OVER (
            PARTITION BY player_id ORDER BY cnt DESC, last_date DESC
        ) AS rn
        FROM (
            SELECT player_id, league, COUNT(*) AS cnt, MAX(date) AS last_date
            FROM (
                SELECT player_id, league, date,
                       ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY date DESC) AS rn2
                FROM tournament_results
                WHERE player_id IN (:playerIds)
            ) ranked
            WHERE rn2 <= 7
            GROUP BY player_id, league
        ) grouped
    ) ranked2
    WHERE rn = 1 AND league IS NOT NULL
""", nativeQuery = true)
    List<PrimaryLeagueProjection> findPrimaryLeagues(@Param("playerIds") Set<UUID> playerIds);


    // TournamentResultRepository
    @Modifying
    @Query("DELETE FROM TournamentResultEntity t WHERE t.playerId = :playerId")
    int deleteByPlayerId(@Param("playerId") UUID playerId);

    @Query("SELECT DAY(tr.date) as day, COALESCE(SUM(tr.amount), 0) as total, COUNT(tr) as count " +
            "FROM TournamentResultEntity tr " +
            "WHERE tr.playerId = :playerId AND tr.date BETWEEN :start AND :end " +
            "GROUP BY DAY(tr.date) " +
            "ORDER BY DAY(tr.date)")
    List<DailyIncomeProjection> getDailyIncome(@Param("playerId") UUID playerId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);

    @Query("SELECT CONCAT(CAST(YEAR(tr.date) AS string), '-', LPAD(CAST(MONTH(tr.date) AS string), 2, '0')) as month, " +
            "SUM(tr.amount) as total, COUNT(tr) as count, AVG(tr.amount) as average " +
            "FROM TournamentResultEntity tr " +
            "WHERE tr.playerId = :playerId AND tr.date >= :since AND YEAR(tr.date) = :year " +
            "GROUP BY YEAR(tr.date), MONTH(tr.date) " +
            "ORDER BY MONTH(tr.date) ASC")
    List<MonthlyIncomeProjection> getMonthlyIncome(@Param("playerId") UUID playerId,
                                                   @Param("since") LocalDate since,
                                                   @Param("year") int year);

    @Query("SELECT COALESCE(AVG(tr.amount), 0) FROM TournamentResultEntity tr " +
            "WHERE tr.playerId = :playerId AND tr.date >= :since")
    double getPlayerAverage(@Param("playerId") UUID playerId, @Param("since") LocalDate since);


    @Query("SELECT tr.league as league, COUNT(tr) as count, SUM(tr.amount) as sum, AVG(tr.amount) as avg " +
            "FROM TournamentResultEntity tr " +
            "WHERE tr.date >= :since " +
            "GROUP BY tr.league " +
            "ORDER BY SUM(tr.amount) DESC")
    List<LeagueStatProjection> getAllLeaguesStats(@Param("since") LocalDate since);

    boolean existsByPlayerIdAndTournament_ExternalId(UUID playerId, Long externalId);


    @Query("SELECT t FROM TournamentResultEntity t WHERE t.playerId = :playerId AND t.date BETWEEN :start AND :end ORDER BY t.date ASC")
    Page<TournamentResultEntity> findByPlayerIdAndDateBetweenOrderByDateAsc(
            @Param("playerId") UUID playerId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);



    @Query("""
    SELECT COUNT(t) as count, COALESCE(SUM(t.amount), 0) as sum,
           COALESCE(AVG(t.amount), 0) as average, COALESCE(SUM(t.amount) * 0.97, 0) as minusThreePercent
    FROM TournamentResultEntity t
    WHERE t.playerId = :playerId AND t.date BETWEEN :start AND :end
    """)
    PeriodStatsProjection getStats(@Param("playerId") UUID playerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    Optional<TournamentResultEntity> findTopByPlayerIdOrderByDateDesc(UUID playerId);

    @Query("""
    SELECT p.id AS playerId,
           p.name AS playerName,
           p.primaryLeague AS primaryLeague,
           COUNT(tr.id) AS tournaments,
           COALESCE(SUM(tr.amount), 0) AS totalAmount,
           COALESCE(AVG(tr.amount), 0) AS averageAmount
    FROM Player p
    LEFT JOIN TournamentResultEntity tr ON tr.playerId = p.id
      AND tr.date BETWEEN :start AND :end
    GROUP BY p.id, p.name, p.primaryLeague
    ORDER BY LOWER(p.name) ASC
""")
    List<PlayerCompareResponse> findPlayersForCompare(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


}