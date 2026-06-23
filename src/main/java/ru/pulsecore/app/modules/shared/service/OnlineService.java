package ru.pulsecore.app.modules.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnlineService {

    private final StringRedisTemplate redis;

    public void markOnline(UUID playerId) {
        redis.opsForValue().set("online:user:" + playerId, "1", Duration.ofSeconds(60));
    }

    public long getOnlineCount() {
        Set<String> keys = redis.keys("online:user:*");
        return keys != null ? keys.size() : 0;
    }
}