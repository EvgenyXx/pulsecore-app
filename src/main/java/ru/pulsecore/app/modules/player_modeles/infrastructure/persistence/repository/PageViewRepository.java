package ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.pulsecore.app.modules.admin_modules.api.dto.PageViewStatsProjection;
import ru.pulsecore.app.modules.shared.model.PageView;

import java.time.Instant;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {


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


}