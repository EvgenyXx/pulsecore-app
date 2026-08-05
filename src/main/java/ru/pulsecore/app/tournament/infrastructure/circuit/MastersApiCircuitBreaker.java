package ru.pulsecore.app.tournament.infrastructure.circuit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MastersApiCircuitBreaker {

    private final RedisTemplate<String, String> redis;

    private static final String FAILURES_KEY = "masters-api:failures";
    private static final String BLOCKED_KEY = "masters-api:blocked";
    private static final int THRESHOLD = 3;
    private static final Duration BLOCK_TTL = Duration.ofMinutes(10);
    private static final Duration FAILURES_TTL = Duration.ofMinutes(10);
    private static final Duration BACKOFF = Duration.ofSeconds(30);

    public boolean isBlocked() {
        return Boolean.TRUE.equals(redis.hasKey(BLOCKED_KEY));
    }

    public void recordFailure() {
        Long count = redis.opsForValue().increment(FAILURES_KEY);
        if (count == null) return;
        redis.expire(FAILURES_KEY, FAILURES_TTL);

        if (count >= THRESHOLD) {
            block();
        }
    }

    public void recordSuccess() {
        unblock();
    }

    public Duration backoff() {
        return BACKOFF;
    }

    private void block() {
        redis.opsForValue().set(BLOCKED_KEY, "1", BLOCK_TTL);
        redis.delete(FAILURES_KEY);
    }

    private void unblock() {
        redis.delete(FAILURES_KEY);
        redis.delete(BLOCKED_KEY);
    }
}