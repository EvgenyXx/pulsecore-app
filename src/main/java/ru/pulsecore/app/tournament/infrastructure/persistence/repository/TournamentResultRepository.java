// ==================== TournamentResultRepository.java ====================
package ru.pulsecore.app.tournament.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PeriodStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.DailyIncomeProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.LeagueStatProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.MonthlyIncomeProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.WeeklyStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TournamentResultRepository extends JpaRepository<TournamentResultEntity, Long> {


    @Query("""
    SELECT p.name AS name, 
           COUNT(tr) AS tournaments, 
           COALESCE(ROUND(SUM(tr.amount)), 0) AS total,
           COALESCE(ROUND(AVG(tr.amount)), 0) AS average
    FROM TournamentResultEntity tr
    JOIN Player p ON tr.playerId = p.id
    WHERE tr.playerId = :playerId 
      AND tr.date BETWEEN :from AND :to
    GROUP BY p.name
    """)
    List<WeeklyStatsProjection> getWeeklyStats(@Param("playerId") UUID playerId,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);


    @Query(value = """
        SELECT league FROM (
            SELECT league, COUNT(*) as cnt
            FROM (
                SELECT league FROM tournament_results
                WHERE player_id = :playerId
                ORDER BY date DESC
                LIMIT 7
            ) last7
            GROUP BY league
            ORDER BY cnt DESC
            LIMIT 1
        ) sub
        """, nativeQuery = true)
    String findPrimaryLeague(@Param("playerId") UUID playerId);





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

    Optional<TournamentResultEntity> findByPlayerIdAndTournament_ExternalId(UUID playerId, Long externalId);

    @Query("SELECT tr.league FROM TournamentResultEntity tr " +
            "WHERE tr.playerId = :playerId ORDER BY tr.date DESC LIMIT 7")
    List<String> findLastLeagues(@Param("playerId") UUID playerId);


}