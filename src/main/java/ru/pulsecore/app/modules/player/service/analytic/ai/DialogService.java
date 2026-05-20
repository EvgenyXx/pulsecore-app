// ==================== 3. DialogService.java ====================
package ru.pulsecore.app.modules.player.service.analytic.ai;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DialogService {

    private final Map<UUID, List<Map<String, String>>> dialogs = new HashMap<>();

    public List<Map<String, String>> getHistory(UUID playerId) {
        return dialogs.computeIfAbsent(playerId, k -> new ArrayList<>());
    }

    public void addExchange(UUID playerId, String question, String answer) {
        List<Map<String, String>> history = getHistory(playerId);
        history.add(Map.of("role", "user", "content", question));
        history.add(Map.of("role", "assistant", "content", answer));
        if (history.size() > 30) {
            history = new ArrayList<>(history.subList(history.size() - 30, history.size()));
        }
        dialogs.put(playerId, history);
    }
}