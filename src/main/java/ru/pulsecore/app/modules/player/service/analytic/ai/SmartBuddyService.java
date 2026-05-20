package ru.pulsecore.app.modules.player.service.analytic.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.modules.shared.properties.SmartProperties;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartBuddyService {


    private final RestTemplate restTemplate = new RestTemplate();
    private final SmartProperties smartProperties;

    public String analyze(String prompt) {
        return ask(List.of(Map.of("role", "user", "content", prompt)));
    }

    public String analyze(List<Map<String, String>> messages) {
        return ask(messages);
    }

    private String ask(List<Map<String, String>> messages) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + smartProperties.getApiKey());

            Map<String, Object> body = Map.of(
                    "model", "deepseek/deepseek-v3.2",
                    "messages", messages,
                    "max_tokens", 500,
                    "temperature", 0.1
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    smartProperties.getApiUrl(), new HttpEntity<>(body, headers), Map.class);

            var choices = (List<Map>) response.getBody().get("choices");
            return (String) ((Map) choices.get(0).get("message")).get("content");
        } catch (Exception e) {
            log.error("SmartBuddy error", e);
            return "Не удалось получить ответ.";
        }
    }
}