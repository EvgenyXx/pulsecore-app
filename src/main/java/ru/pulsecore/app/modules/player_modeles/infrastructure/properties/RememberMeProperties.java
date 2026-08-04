package ru.pulsecore.app.modules.player_modeles.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.remember-me")
public class RememberMeProperties {
    private String cookieName;
    private int maxAgeSeconds;
    private String sameSite;
}