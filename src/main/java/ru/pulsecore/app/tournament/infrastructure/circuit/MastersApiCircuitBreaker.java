package ru.pulsecore.app.tournament.infrastructure.circuit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.admin.MastersApiUnavailableContext;
import ru.pulsecore.app.shared.event.MailNotificationEvent;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class MastersApiCircuitBreaker {

    private final RedisTemplate<String, String> redis;
    private final ApplicationEventPublisher publishEvent;

    private static final String FAILURES_KEY = "masters-api:failures";
    private static final String BLOCKED_KEY = "masters-api:blocked";
    private static final String NOTIFIED_KEY = "masters-api:notified";

    private static final int THRESHOLD = 1;
    private static final Duration BLOCK_TTL = Duration.ofMinutes(10);
    private static final Duration FAILURES_TTL = Duration.ofMinutes(15);
    private static final Duration BACKOFF = Duration.ofSeconds(60);



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

        if (!Boolean.TRUE.equals(redis.hasKey(NOTIFIED_KEY))) {
            redis.opsForValue().set(NOTIFIED_KEY, "1", BLOCK_TTL);
            log.error("Masters API недоступен, запросы заблокированы на 15 минут");
            publishEvent.publishEvent(new MailNotificationEvent(
                    MailTypes.ADMIN_MASTERS_UNAVAILABLE,
                    new MastersApiUnavailableContext("Masters API недоступен")));
        }
    }

    private void unblock() {
        redis.delete(FAILURES_KEY);
        redis.delete(BLOCKED_KEY);
    }
}