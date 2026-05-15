package ru.pulsecore.app.modules.player.service.analytic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pulsecore.app.modules.shared.propirties.GigaChatProperties;

import java.util.*;

@Slf4j
@Service
public class OpenRouterService {

    private final String apiKey;
    private final String apiUrl = "https://openrouter.ai/api/v1/chat/completions";
    private final RestTemplate restTemplate;

    public OpenRouterService(GigaChatProperties props) {
        this.apiKey = props.getAuthKey();
        this.restTemplate = new RestTemplate();
    }

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
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "https://pulsecore-app.ru");
            headers.set("X-Title", "PulseCore");

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", "google/gemma-3-4b-it:free");
            body.put("messages", messages);
            body.put("max_tokens", 500);
            body.put("temperature", 0.7);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl, new HttpEntity<>(body, headers), Map.class);

            var choices = (List<Map>) response.getBody().get("choices");
            var message = (Map) choices.get(0).get("message");
            String content = (String) message.get("content");

            return content != null ? content.trim() : "Нет ответа";
        } catch (Exception e) {
            log.error("OpenRouter API error", e);
            return "Не удалось получить ответ. Попробуйте позже.";
        }
    }
}