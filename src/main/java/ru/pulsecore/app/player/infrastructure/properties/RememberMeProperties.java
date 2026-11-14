package ru.pulsecore.app.player.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.remember-me")
public record RememberMeProperties(
        String cookieName,
        int maxAgeSeconds,
        String sameSite
) {}