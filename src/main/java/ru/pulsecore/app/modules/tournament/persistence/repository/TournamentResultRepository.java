package ru.pulsecore.app.modules.tournament.persistence.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.core.dto.TopPlayerProjection;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentResultRepository extends JpaRepository<TournamentResultEntity, Long> {

    @Query("SELECT p.name as name, " +
            "COALESCE((SELECT SUM(trAll.amount) FROM TournamentResultEntity trAll WHERE trAll.player = p AND trAll.date >= :since), 0) as total, " +
            "COALESCE((SELECT COUNT(trAll) FROM TournamentResultEntity trAll WHERE trAll.player = p AND trAll.date >= :since), 0) as tournaments " +
            "FROM TournamentResultEntity tr JOIN tr.player p " +
            "WHERE tr.date >= :since AND tr.league = :league " +
            "AND tr.player.id IN (" +
            "    SELECT tr2.player.id FROM TournamentResultEntity tr2 " +
            "    WHERE tr2.date >= :since " +
            "    GROUP BY tr2.player.id " +
            "    HAVING SUM(CASE WHEN tr2.league = :league THEN 1 ELSE 0 END) > " +
            "           SUM(CASE WHEN tr2.league != :league THEN 1 ELSE 0 END) " +
            "    OR (" +
            "        SUM(CASE WHEN tr2.league = :league THEN 1 ELSE 0 END) = " +
            "        SUM(CASE WHEN tr2.league != :league THEN 1 ELSE 0 END) " +
            "        AND (" +
            "            SELECT tr3.league FROM TournamentResultEntity tr3 " +
            "            WHERE tr3.player = tr2.player AND tr3.date >= :since " +
            "            ORDER BY tr3.date DESC LIMIT 1" +
            "        ) = :league" +
            "    )" +
            ") " +
            "GROUP BY p.id, p.name " +
            "ORDER BY total DESC " +
            "LIMIT :limit")
    List<TopPlayerProjection> findTopByPrimaryLeague(@Param("since") LocalDate since,
                                                     @Param("league") String league,
                                                     @Param("limit") int limit);

    @Query("SELECT tr.player.id, SUM(tr.amount) " +
            "FROM TournamentResultEntity tr " +
            "WHERE tr.date BETWEEN :since AND :until " +
            "GROUP BY tr.player.id")
    List<Object[]> getAllPlayerStats(@Param("since") LocalDate since, @Param("until") LocalDate until);

    @Query("SELECT COUNT(DISTINCT tr.player.id) FROM TournamentResultEntity tr " +
            "WHERE tr.date BETWEEN :since AND :until")
    long countPlayersWithEarnings(@Param("since") LocalDate since, @Param("until") LocalDate until);

    @Query("SELECT p.name as name, SUM(tr.amount) as total, COUNT(*) as tournaments " +
            "FROM TournamentResultEntity tr JOIN tr.player p " +
            "WHERE tr.date >= :since " +
            "GROUP BY tr.player.id, p.name " +
            "ORDER BY SUM(tr.amount) DESC " +
            "LIMIT :limit")
    List<TopPlayerProjection> findTopPlayers(@Param("since") LocalDate since, @Param("limit") int limit);

    boolean existsByPlayerAndTournament_ExternalId(Player player, Long externalId);

    List<TournamentResultEntity> findByPlayerAndDateBetweenOrderByDateAsc(Player player, LocalDate start, LocalDate end);

    @Query("""
    SELECT COUNT(t) as count, COALESCE(SUM(t.amount), 0) as sum,
           COALESCE(AVG(t.amount), 0) as average, COALESCE(SUM(t.amount) * 0.97, 0) as minusThreePercent
    FROM TournamentResultEntity t
    WHERE t.player = :player AND t.date BETWEEN :start AND :end
    """)
    PeriodStatsProjection getStats(Player player, LocalDate start, LocalDate end);

    Optional<TournamentResultEntity> findTopByPlayerOrderByDateDesc(Player player);
    Optional<TournamentResultEntity> findByPlayerAndTournament_ExternalId(Player player, Long externalId);

    @Query("SELECT tr.league FROM TournamentResultEntity tr " +
            "WHERE tr.player = :player ORDER BY tr.date DESC LIMIT 7")
    List<String> findLastLeagues(@Param("player") Player player);
}