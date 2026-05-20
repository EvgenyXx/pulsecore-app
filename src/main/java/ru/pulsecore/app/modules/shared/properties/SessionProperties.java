package ru.pulsecore.app.modules.shared.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class SessionProperties {
    private String name;
    private boolean httpOnly;
    private boolean secure;
    private String maxAge;
}