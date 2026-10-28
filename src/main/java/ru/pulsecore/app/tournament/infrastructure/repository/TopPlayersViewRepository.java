package ru.pulsecore.app.tournament.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.tournament.domain.entity.TopPlayersView;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopPlayersViewRepository extends JpaRepository<TopPlayersView, UUID> {

    @Query("SELECT t FROM TopPlayersView t WHERE t.period = :period AND t.tournaments > 0 ORDER BY t.total DESC")
    List<TopPlayersView> findByPeriodOrderByTotalDesc(@Param("period") String period);

    @Query("SELECT t FROM TopPlayersView t WHERE t.period = :period AND t.primaryLeague = :league AND t.tournaments > 0 ORDER BY t.total DESC")
    List<TopPlayersView> findByPeriodAndPrimaryLeagueOrderByTotalDesc(@Param("period") String period, @Param("league") String league);
}