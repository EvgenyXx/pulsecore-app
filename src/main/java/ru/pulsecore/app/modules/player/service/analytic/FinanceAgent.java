package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.StatProjection;
import ru.pulsecore.app.modules.player.domain.Player;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinanceAgent implements Agent {

    private final StatsEngine statsEngine;
    private final GigaChatService ai;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public boolean canHandle(String question) {
        String q = question.toLowerCase();
        return !q.contains("лиг") && !q.contains("рейтинг") && !q.contains("топ") &&
                !q.contains("сравн") && !q.contains("лучш") && !q.contains("худш") &&
                !q.contains("больше всего") && !q.contains("меньше всего") && !q.contains("рекорд");
    }

    @Override
    public String answer(Player player, String question) {
        String firstName = extractFirstName(player.getName());

        // StatsEngine — без истории, каждый запрос новый
        StatProjection stats = statsEngine.calculate(player, question);
        log.info("Stats: {}", stats.toContextString());

        // Формируем ответ — без истории диалога
        String system = String.format("""
            Ты — финансовый ассистент. Отвечай СТРОГО по шаблону.
            
            ШАБЛОН: "%s, за ПЕРИОД ты заработал СУММА за КОЛИЧЕСТВО турниров"
            
            ПРИМЕРЫ:
            - "Евгений, за апрель 2026 ты заработал 131 800 рублей за 22 турнира"
            - "Евгений, за 2025 год ты заработал 844 850 рублей за 181 турнир"
            - "Евгений, с 27 апреля по сегодня ты заработал 53 100 рублей за 9 турниров"
            
            ПРАВИЛА:
            - Числа с пробелами: 131 800
            - НЕ меняй цифры
            - Без markdown
            """, firstName);

        String userMsg = String.format("""
            ДАННЫЕ:
            Период: %s - %s
            Сумма: %.0f руб
            Турниров: %d
            
            ВОПРОС: %s
            Ответь строго по шаблону.
            """,
                stats.getStart().format(FMT), stats.getEnd().format(FMT),
                stats.getSum(), stats.getCount(),
                question);

        String answer = ai.analyze(system + "\n\n" + userMsg);
        return answer.replaceAll("\\*\\*", "").replaceAll("__", "").replaceAll("\\*", "");
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        String name = parts.length >= 2 ? parts[1] : parts[0];
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}