package ru.pulsecore.app.modules.push.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.vapid")
public class VapidConfig {
    private String publicKey;
    private String privateKey;
}