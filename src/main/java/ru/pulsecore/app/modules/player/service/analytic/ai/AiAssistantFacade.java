// ==================== 5. AiAssistantFacade.java ====================
package ru.pulsecore.app.modules.player.service.analytic.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.domain.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiAssistantFacade {

    private final SmartBuddyService ai;
    private final PlayerDataAggregator dataAggregator;
    private final DialogService dialogService;
    private final PromptFactory promptFactory;
    private final WeeklyAnalyzer weeklyAnalyzer;

    public String chat(Player player, String question) {
        String firstName = extractFirstName(player.getName());
        LocalDate today = LocalDate.now();
        String allData = dataAggregator.gatherData(player, today);

        String system = promptFactory.createChatSystemPrompt(player.getName(), firstName, today);
        String userMsg = promptFactory.createChatUserMessage(allData, question);

        List<Map<String, String>> history = dialogService.getHistory(player.getId());
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", system));
        messages.addAll(history);
        messages.add(Map.of("role", "user", "content", userMsg));

        String answer = cleanMarkdown(ai.analyze(messages));
        dialogService.addExchange(player.getId(), question, answer);

        return answer;
    }

//    public String analyzeWeek(Player player) {
//        return weeklyAnalyzer.analyze(player);
//    }

    private String cleanMarkdown(String text) {
        return text.replaceAll("\\*\\*", "").replace("__", "").replaceAll("\\*", "");
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        String name = parts.length >= 2 ? parts[1] : parts[0];
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}