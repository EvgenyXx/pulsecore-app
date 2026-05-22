// ==================== TopPlayersViewRepository.java ====================
package ru.pulsecore.app.modules.tournament.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pulsecore.app.modules.player.domain.TopPlayersView;
import java.util.List;
import java.util.UUID;

@Repository
public interface TopPlayersViewRepository extends JpaRepository<TopPlayersView, UUID> {
    List<TopPlayersView> findByPeriodOrderByTotalDesc(String period);
    List<TopPlayersView> findByPeriodAndPrimaryLeagueOrderByTotalDesc(String period, String league);
}