package ru.pulsecore.app.tournament.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.masters-api")
public record MastersApiProperties(
        Admin admin,
        Get get
) {
    public record Admin(
            String url,
            String method,
            String userAgent,
            String action,
            String country,
            int timeout
    ) {}

    public record Get(
            String url,
            String method,
            String userAgent,
            String action,
            String country,
            int timeout
    ) {}
}