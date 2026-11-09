package ru.pulsecore.app.tournament.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.CacheNames;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheEvictionService {

    private final CacheManager cacheManager;

    public void evictAnalytics() {
        clear(CacheNames.ANALYTICS);
        clear(CacheNames.MONTHLY_INCOME);
        clear(CacheNames.DAILY_INCOME);
        clear(CacheNames.BEST_TIME);
        log.debug("Кеш аналитики очищен");
    }

    public void evictHallOfFame() {
        clear(CacheNames.TOP_ALL);
        clear(CacheNames.TOP_LEAGUE);
        log.debug("Кеш зала славы очищен");
    }

    private void clear(String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            cache.clear();
        }
    }
}