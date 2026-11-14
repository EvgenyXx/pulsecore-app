package ru.pulsecore.app.player.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record QrProperties(
        String baseUrl,
        String qrColor
) {
    public QrProperties {
        if (qrColor == null) {
            qrColor = "#000000";
        }
    }
}