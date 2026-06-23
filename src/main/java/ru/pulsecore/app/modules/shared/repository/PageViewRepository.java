// modules/shared/repository/PageViewRepository.java
package ru.pulsecore.app.modules.shared.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.pulsecore.app.modules.admin.dto.PageViewStatsProjection;
import ru.pulsecore.app.modules.shared.model.PageView;

import java.time.Instant;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    @Query(value = "SELECT COUNT(DISTINCT player_id) FROM page_views WHERE created_at >= :since", nativeQuery = true)
    long countUniqueVisitorsSince(@Param("since") Instant since);

    // PageViewRepository.java — метод
    @Query(value = """
    SELECT p.name::VARCHAR as name,
           pv.path::VARCHAR as path,
           pv.method::VARCHAR as method,
           COUNT(*) as count
    FROM page_views pv
    JOIN players p ON p.id = pv.player_id
    WHERE pv.created_at >= :since
      AND pv.path NOT LIKE '/api/admin/%'
      AND pv.email != 'evgenypavlov666@yandex.ru'
      AND pv.path != '/api/auth/me'
    GROUP BY p.name, pv.path, pv.method
    ORDER BY p.name, count DESC
    LIMIT 100
""", nativeQuery = true)
    List<PlayerPageViewProjection> findPlayerStatsSince(Instant since);

    @Query(value = """
    SELECT path, method, COUNT(*) as count
    FROM page_views
    WHERE created_at >= :since
      AND path NOT LIKE '/api/admin/%'
      AND email != 'evgenypavlov666@yandex.ru'
      AND path != '/api/auth/me'
    GROUP BY path, method
    ORDER BY count DESC
    LIMIT 50
""", nativeQuery = true)
    List<PageViewStatsProjection> findStatsSince(Instant since);

    List<PageView> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to, Pageable pageable);

    List<PageView> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email, Pageable pageable);

    List<PageView> findByEmailIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtDesc(String email, Instant from, Instant to, Pageable pageable);
}