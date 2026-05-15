package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AgentRouter {

    private final List<Agent> agents;

    // Хранилище истории: playerId -> последние сообщения
    private final Map<UUID, List<String>> history = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, List<String>> eldest) {
            return size() > 500;
        }
    };

    public String route(Player player, String question) {
        // Добавляем вопрос в историю
        List<String> playerHistory = history.computeIfAbsent(player.getId(), k -> new ArrayList<>());
        playerHistory.add(question);
        if (playerHistory.size() > 10) playerHistory.remove(0);

        // Объединяем историю с текущим вопросом для контекста
        String fullContext = String.join(" | ", playerHistory);

        // Выбираем агентов
        List<Agent> active = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent.canHandle(question)) {
                active.add(agent);
            }
        }

        String q = question.toLowerCase();
        boolean isComparison = q.contains("сравн") || q.contains("лучш") || q.contains("худш") ||
                q.contains("больше всего") || q.contains("меньше всего") ||
                q.contains("рекорд") || q.contains("самый");

        if (isComparison) {
            active.removeIf(a -> !(a instanceof ComparisonAgent));
        }

        if (active.isEmpty()) {
            // FinanceAgent как дефолтный
            for (Agent agent : agents) {
                if (agent instanceof FinanceAgent) {
                    active.add(agent);
                    break;
                }
            }
        }

        for (Agent agent : active) {
            try {
                // Передаём полный контекст (история + текущий вопрос)
                String answer = agent.answer(player, fullContext);
                if (answer != null && !answer.isBlank()) {
                    // Сохраняем ответ в историю
                    playerHistory.add(answer);
                    if (playerHistory.size() > 10) playerHistory.remove(0);
                    history.put(player.getId(), playerHistory);
                    return answer;
                }
            } catch (Exception e) {
                // next agent
            }
        }

        return "Не смог найти ответ.";
    }
}