package ru.pulsecore.app.tournament.application.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerPauseService {

    private static final String PAUSE_KEY = "scheduler:paused";
    private static final Duration PAUSE_TTL = Duration.ofHours(24);

    private final RedisTemplate<String, String> redis;

    public void pause() {
        redis.opsForValue().set(PAUSE_KEY, "true", PAUSE_TTL);
        log.info("Планировщик поставлен на паузу на 24 часа");
    }

    public void resume() {
        redis.delete(PAUSE_KEY);
        log.info("Планировщик возобновлён");
    }

    public boolean isPaused() {
        return Boolean.TRUE.equals(redis.hasKey(PAUSE_KEY));
    }
}