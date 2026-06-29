package ru.pulsecore.app.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final RedisSerializationContext.SerializationPair<String> KEY_SERIALIZER =
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

    private static final RedisSerializationContext.SerializationPair<Object> VALUE_SERIALIZER =
            RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(CacheNames.SUBSCRIPTION,
                        defaultConfig().entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CacheNames.PRICES,
                        defaultConfig().entryTtl(Duration.ofDays(30)))
                .cacheDefaults(
                        defaultConfig().entryTtl(Duration.ofMinutes(5)));
    }

    private static RedisCacheConfiguration defaultConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(KEY_SERIALIZER)
                .serializeValuesWith(VALUE_SERIALIZER);
    }
}