package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.PlayerStatsService;

@Component
@RequiredArgsConstructor
public class LeagueAgent implements Agent {

    private final PlayerStatsService statsService;

    @Override
    public boolean canHandle(String question) {
        String q = question.toLowerCase();
        return q.contains("лиг") || q.contains("league") ||
                q.contains("рейтинг") || q.contains("топ") || q.contains("позиц");
    }

    @Override
    public String answer(Player player, String question) {
        String league = statsService.getPrimaryLeague(player.getId());
        int position = statsService.getTopWithPosition(player.getId()).getPlayerPosition();

        return String.format("Основная лига: %s. Позиция в топе недели: %d", league, position);
    }
}