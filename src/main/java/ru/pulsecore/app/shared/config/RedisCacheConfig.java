package ru.pulsecore.app.shared.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final RedisSerializationContext.SerializationPair<String> KEY_SERIALIZER =
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

    private static final RedisSerializationContext.SerializationPair<Object> VALUE_SERIALIZER =
            RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer());

    private static GenericJackson2JsonRedisSerializer redisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new GenericJackson2JsonRedisSerializer(mapper);
    }

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