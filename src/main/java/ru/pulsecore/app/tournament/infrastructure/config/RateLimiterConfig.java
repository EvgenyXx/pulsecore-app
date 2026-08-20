package ru.pulsecore.app.tournament.infrastructure.config;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    public static final String CANCELED_RATE_LIMITER = "canceledRateLimiter";
    public static final String FINISH_RATE_LIMITER = "finishRateLimiter";
    public static final String HISTORY_SYNC_RATE_LIMITER = "historySyncRateLimiter";

    @Bean(CANCELED_RATE_LIMITER)
    public Bucket canceledRateLimiter() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(1)
                        .refillIntervally(1, Duration.ofSeconds(2))
                        .build())
                .build();
    }

    @Bean(FINISH_RATE_LIMITER)
    public Bucket finishRateLimiter() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(1)
                        .refillIntervally(1, Duration.ofSeconds(1))
                        .build())
                .build();
    }


    @Bean(HISTORY_SYNC_RATE_LIMITER)
    public Bucket tournamentRateLimiter() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(1)
                        .refillIntervally(1, Duration.ofSeconds(2))
                        .build())
                .build();
    }
}