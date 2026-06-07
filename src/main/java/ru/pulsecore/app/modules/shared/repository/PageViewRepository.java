// modules/shared/repository/PageViewRepository.java
package ru.pulsecore.app.modules.shared.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pulsecore.app.modules.shared.model.PageView;

import java.time.Instant;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    @Query(value = """
        SELECT path, method, COUNT(*) as count
        FROM page_views
        WHERE created_at >= :since
        GROUP BY path, method
        ORDER BY count DESC
        LIMIT 50
    """, nativeQuery = true)
    List<PageViewStatsProjection> findStatsSince(Instant since);

    List<PageView> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to, Pageable pageable);

    List<PageView> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email, Pageable pageable);

    List<PageView> findByEmailIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtDesc(String email, Instant from, Instant to, Pageable pageable);
}