package ru.pulsecore.app.modules.shared.propirties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.gigachat")
public class GigaChatProperties {
    private String authKey;
    private String authUrl = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
    private String apiUrl = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions";
    private String scope = "GIGACHAT_API_PERS";
}