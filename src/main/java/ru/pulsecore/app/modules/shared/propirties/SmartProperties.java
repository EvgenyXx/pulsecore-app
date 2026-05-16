package ru.pulsecore.app.modules.shared.propirties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.smart")
public class SmartProperties {


    private String apiKey;
    private String apiUrl;

}