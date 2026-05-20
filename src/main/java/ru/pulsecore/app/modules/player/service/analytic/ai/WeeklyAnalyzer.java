// ==================== 4. WeeklyAnalyzer.java ====================
package ru.pulsecore.app.modules.player.service.analytic.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.league.LeagueService;
import ru.pulsecore.app.modules.player.service.analytic.top.TopWeekService;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeeklyAnalyzer {

    private final TournamentResultService resultService;
    private final LeagueService leagueService;
    private final TopWeekService topWeekService;
    private final SmartBuddyService ai;
    private final PromptFactory promptFactory;

    public String analyze(Player player) {
        LocalDate today = LocalDate.now();
        String firstName = extractFirstName(player.getName());

        PeriodStatsProjection thisWeek = resultService.getStatsByPeriod(player, today.minusDays(6), today);
        PeriodStatsProjection lastWeek = resultService.getStatsByPeriod(player, today.minusDays(13), today.minusDays(7));

        double thisSum = thisWeek != null ? thisWeek.getSum() : 0;
        long thisCount = thisWeek != null ? thisWeek.getCount() : 0;
        double thisAvg = thisCount > 0 ? thisSum / thisCount : 0;
        double lastSum = lastWeek != null ? lastWeek.getSum() : 0;
        long lastCount = lastWeek != null ? lastWeek.getCount() : 0;
        double lastAvg = lastCount > 0 ? lastSum / lastCount : 0;

        String league = leagueService.getPrimaryLeague(player.getId());
        int position = topWeekService.getTopWithPosition(player.getId()).getPlayerPosition();

        String prompt = promptFactory.createWeeklyAnalysisPrompt(
                firstName, thisSum, thisCount, thisAvg,
                lastSum, lastCount, lastAvg, league, position);

        return cleanMarkdown(ai.analyze(prompt));
    }

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