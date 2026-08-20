package ru.pulsecore.app.tournament.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LineupHashCache {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofHours(6);

    public boolean hasSameHash(LocalDate date, String hash) {
        String cached = redisTemplate.opsForValue().get(key(date));
        return hash.equals(cached);
    }

    public void update(LocalDate date, String hash) {
        redisTemplate.opsForValue().set(key(date), hash, TTL);
    }

    private String key(LocalDate date) {
        return "lineup:hash:" + date;
    }
}