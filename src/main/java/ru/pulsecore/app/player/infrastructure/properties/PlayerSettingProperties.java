package ru.pulsecore.app.player.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.settings.player")
public record PlayerSettingProperties(
        Subscription subscription
) {
    public record Subscription(int trialDays) {}
}