package ru.pulsecore.app.shared.infrastructure.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {

    private String dir = "audit";
}