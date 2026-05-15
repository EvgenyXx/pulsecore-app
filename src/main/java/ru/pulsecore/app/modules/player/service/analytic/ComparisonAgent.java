package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.StatProjection;
import ru.pulsecore.app.modules.player.domain.Player;

@Component
@RequiredArgsConstructor
public class ComparisonAgent implements Agent {

    private final StatsEngine statsEngine;
    private final GigaChatService ai;

    @Override
    public boolean canHandle(String question) {
        String q = question.toLowerCase();
        return q.contains("сравн") || q.contains("лучш") || q.contains("худш") ||
                q.contains("больше всего") || q.contains("меньше всего") ||
                q.contains("рекорд") || q.contains("самый");
    }

    @Override
    public String answer(Player player, String question) {
        String firstName = extractFirstName(player.getName());

        // StatsEngine считает
        StatProjection stats = statsEngine.calculate(player, question);

        // ИИ формулирует
        String prompt = String.format("""
            Ты — персональный ассистент %s. Отвечай на "ты", обращайся: %s.
            
            ГОТОВЫЕ ДАННЫЕ (не пересчитывай):
            %s
            
            ВОПРОС: %s
            
            ОТВЕТ: 1-2 предложения, без markdown. Сравни цифры если есть дети.
            """, player.getName(), firstName, stats.toContextString(), question);

        String answer = ai.analyze(prompt);
        return answer.replaceAll("\\*\\*", "").replaceAll("__", "");
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        String name = parts.length >= 2 ? parts[1] : parts[0];
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}