package ru.pulsecore.app.modules.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheEvictionService {

    private final CacheManager cacheManager;

    public void evictAnalytics() {
        cacheManager.getCache("analytics").clear();
        cacheManager.getCache("monthly_income").clear();
        cacheManager.getCache("daily_income").clear();
        cacheManager.getCache("best_time").clear();
    }


    public void evictHallOfFame() {
        cacheManager.getCache("top-all").clear();
        cacheManager.getCache("top-league").clear();
    }
}
