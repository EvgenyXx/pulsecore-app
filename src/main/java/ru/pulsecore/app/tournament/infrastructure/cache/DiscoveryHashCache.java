package ru.pulsecore.app.tournament.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscoveryHashCache {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofHours(6);

    public boolean hasSameHash(String date, Long tournamentId, String hash) {
        String cached = redisTemplate.opsForValue().get(key(date, tournamentId));
        return hash.equals(cached);
    }

    public void update(String date, Long tournamentId, String hash) {
        redisTemplate.opsForValue().set(key(date, tournamentId), hash, TTL);
    }

    public void delete(String date, Long tournamentId) {
        redisTemplate.delete(key(date, tournamentId));
    }

    public String get(String date, Long tournamentId) {
        return redisTemplate.opsForValue().get(key(date, tournamentId));
    }

    private String key(String date, Long tournamentId) {
        return "discovery:hash:" + date + ":" + tournamentId;
    }
}