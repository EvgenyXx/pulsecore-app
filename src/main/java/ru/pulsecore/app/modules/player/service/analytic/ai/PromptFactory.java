// ==================== 1. PromptFactory.java ====================
package ru.pulsecore.app.modules.player.service.analytic.ai;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PromptFactory {

    public String createChatSystemPrompt(String playerName, String firstName, LocalDate today) {
        return String.format("""
            Ты — персональный ассистент %s. Отвечай на "ты", обращайся: %s.
            Сегодня: %s.
            
            У тебя есть доступ ко ВСЕМ данным игрока ниже. Ты можешь:
            - Отвечать на ЛЮБЫЕ вопросы о заработке, турнирах, статистике
            - Сравнивать периоды, находить лучшие/худшие результаты
            - Анализировать по времени, дням недели, лигам
            - Выписывать турниры списком, считать средние
            - Объяснять цифры и давать рекомендации
            
            ПРАВИЛА:
            - Используй ТОЛЬКО данные ниже, не выдумывай
            - Отвечай как живой человек, 1-5 предложений
            - Без markdown
            - Если просят список — столбиком с датами и суммами
            - Если просят анализ по времени/дням — посчитай сам из данных
            - Если не знаешь — честно скажи
            """, playerName, firstName, today);
    }

    public String createChatUserMessage(String allData, String question) {
        return String.format("""
            ДАННЫЕ ИГРОКА:
            %s
            
            ВОПРОС: %s
            """, allData, question);
    }

    public String createWeeklyAnalysisPrompt(String firstName, double thisSum, long thisCount,
                                             double thisAvg, double lastSum, long lastCount,
                                             double lastAvg, String league, int position) {
        return String.format("""
            Проанализируй недельную статистику игрока %s.
            
            Текущая неделя: %.0f руб, %d турниров, средний чек %.0f руб
            Прошлая неделя: %.0f руб, %d турниров, средний чек %.0f руб
            Основная лига: %s
            Позиция в топе: %d
            
            Дай короткий анализ (2-3 предложения): сравни недели, тренд среднего чека, один совет.
            Отвечай на "ты", без markdown, с цифрами.
            """, firstName, thisSum, thisCount, thisAvg,
                lastSum, lastCount, lastAvg, league, position);
    }
}