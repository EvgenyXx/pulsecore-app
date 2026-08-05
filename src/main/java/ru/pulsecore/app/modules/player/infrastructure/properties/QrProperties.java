package ru.pulsecore.app.modules.player.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class QrProperties {
    private String baseUrl;
    private String qrColor = "#000000";
}