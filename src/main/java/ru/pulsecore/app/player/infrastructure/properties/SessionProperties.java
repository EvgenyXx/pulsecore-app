package ru.pulsecore.app.player.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public record SessionProperties(
        String name,
        boolean httpOnly,
        boolean secure,
        int maxAge,
        String path,
        String sameSite
) {
    public SessionProperties {
        if (path == null) {
            path = "/";
        }
    }
}