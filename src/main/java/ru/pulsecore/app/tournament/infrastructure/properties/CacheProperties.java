package ru.pulsecore.app.tournament.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Data
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {
    /**
     * Время жизни хеша (например: 6h, 2h, 30m).
     */
    private Duration discoveryHashTtl = Duration.ofHours(6);
}