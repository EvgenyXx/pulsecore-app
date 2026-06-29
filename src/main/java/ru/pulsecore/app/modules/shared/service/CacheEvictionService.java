package ru.pulsecore.app.modules.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.config.CacheNames;

@Service
@RequiredArgsConstructor
public class CacheEvictionService {

    private final CacheManager cacheManager;

    public void evictAnalytics() {
        clear(CacheNames.ANALYTICS);
        clear(CacheNames.MONTHLY_INCOME);
        clear(CacheNames.DAILY_INCOME);
        clear(CacheNames.BEST_TIME);
    }

    public void evictHallOfFame() {
        clear(CacheNames.TOP_ALL);
        clear(CacheNames.TOP_LEAGUE);
    }

    private void clear(String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            cache.clear();
        }
    }
}