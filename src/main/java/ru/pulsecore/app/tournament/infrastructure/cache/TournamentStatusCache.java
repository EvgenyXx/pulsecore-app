package ru.pulsecore.app.tournament.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TournamentStatusCache {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(30);

    public boolean isInProgress(String link) {
        String cached = redisTemplate.opsForValue().get(key(link));
        return "IN_PROGRESS".equals(cached);
    }

    public void setInProgress(String link) {
        redisTemplate.opsForValue().set(key(link), "IN_PROGRESS", TTL);
    }

    public void remove(String link) {
        redisTemplate.delete(key(link));
    }

    private String key(String link) {
        return "tournament:status:" + link;
    }
}