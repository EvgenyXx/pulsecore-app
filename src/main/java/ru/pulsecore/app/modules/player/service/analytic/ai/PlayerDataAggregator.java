// ==================== 2. PlayerDataAggregator.java ====================
package ru.pulsecore.app.modules.player.service.analytic.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.player.api.dto.TopWeekResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.analytic.league.LeagueService;
import ru.pulsecore.app.modules.player.service.analytic.top.TopWeekService;
import ru.pulsecore.app.modules.tournament.api.dto.LeagueStatProjection;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerDataAggregator {

    private final TournamentResultService resultService;
    private final TopWeekService topWeekService;
    private final LeagueService leagueService;
    private final TournamentResultRepository tournamentResultRepository;
    private final LineupRepository lineupRepository;

    private static final String[] MONTHS = {
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    public String gatherData(Player player, LocalDate today) {
        StringBuilder sb = new StringBuilder();
        appendRecentTournaments(sb, player, today);
        appendCurrentWeek(sb, player, today);
        appendLastWeek(sb, player, today);
        appendCurrentMonth(sb, player, today);
        appendMonthlyHistory(sb, player, today);
        appendYearlyTotals(sb, player, today);
        appendLeagueStats(sb, player);
        appendTopWeek(sb, player);
        appendUpcomingLineups(sb, player, today);
        appendPrimaryLeague(sb, player);
        return sb.toString();
    }

    private void appendRecentTournaments(StringBuilder sb, Player player, LocalDate today) {
        List<TournamentResultEntity> all = resultService.getResultsByPeriod(
                player, LocalDate.of(2020, 1, 1), today);
        if (all.size() > 200) all = all.subList(all.size() - 200, all.size());

        sb.append("=== ВСЕ ТУРНИРЫ (последние ").append(all.size()).append(") ===\n");
        sb.append("Формат: ДАТА | ВРЕМЯ | СУММА | ЛИГА\n");
        for (TournamentResultEntity r : all) {
            String time = (r.getTournament() != null && r.getTournament().getTime() != null)
                    ? r.getTournament().getTime() : "??:??";
            sb.append(String.format("%s | %s | %.0f руб | Лига %s\n",
                    r.getDate(), time, r.getAmount(), r.getLeague()));
        }
    }

    private void appendCurrentWeek(StringBuilder sb, Player player, LocalDate today) {
        PeriodStatsProjection week = resultService.getStatsByPeriod(player, today.minusDays(6), today);
        sb.append(String.format("\n=== ТЕКУЩАЯ НЕДЕЛЯ ===\n%.0f руб, %d турниров\n",
                week != null ? week.getSum() : 0, week != null ? week.getCount() : 0));
    }

    private void appendLastWeek(StringBuilder sb, Player player, LocalDate today) {
        PeriodStatsProjection prevWeek = resultService.getStatsByPeriod(player, today.minusDays(13), today.minusDays(7));
        sb.append(String.format("=== ПРОШЛАЯ НЕДЕЛЯ ===\n%.0f руб, %d турниров\n",
                prevWeek != null ? prevWeek.getSum() : 0, prevWeek != null ? prevWeek.getCount() : 0));
    }

    private void appendCurrentMonth(StringBuilder sb, Player player, LocalDate today) {
        PeriodStatsProjection curMonth = resultService.getStatsByPeriod(player, today.withDayOfMonth(1), today);
        sb.append(String.format("=== ТЕКУЩИЙ МЕСЯЦ ===\n%.0f руб, %d турниров\n",
                curMonth != null ? curMonth.getSum() : 0, curMonth != null ? curMonth.getCount() : 0));
    }

    private void appendMonthlyHistory(StringBuilder sb, Player player, LocalDate today) {
        for (int year = 2025; year <= today.getYear(); year++) {
            sb.append(String.format("\n=== %d ГОД ПОМЕСЯЧНО ===\n", year));
            int maxMonth = (year == today.getYear()) ? today.getMonthValue() : 12;
            for (int m = 1; m <= maxMonth; m++) {
                LocalDate start = LocalDate.of(year, m, 1);
                LocalDate end = (year == today.getYear() && m == today.getMonthValue())
                        ? today : start.plusMonths(1).minusDays(1);
                PeriodStatsProjection st = resultService.getStatsByPeriod(player, start, end);
                sb.append(String.format("- %s: %.0f руб, %d турниров\n",
                        MONTHS[m - 1], st != null ? st.getSum() : 0, st != null ? st.getCount() : 0));
            }
        }
    }

    private void appendYearlyTotals(StringBuilder sb, Player player, LocalDate today) {
        sb.append("\n=== ИТОГИ ===\n");
        PeriodStatsProjection y2025 = resultService.getStatsByPeriod(player, LocalDate.of(2025,1,1), LocalDate.of(2025,12,31));
        sb.append(String.format("- 2025: %.0f руб, %d турниров\n",
                y2025 != null ? y2025.getSum() : 0, y2025 != null ? y2025.getCount() : 0));
        PeriodStatsProjection y2026 = resultService.getStatsByPeriod(player, LocalDate.of(2026,1,1), today);
        sb.append(String.format("- 2026: %.0f руб, %d турниров\n",
                y2026 != null ? y2026.getSum() : 0, y2026 != null ? y2026.getCount() : 0));
    }

    private void appendLeagueStats(StringBuilder sb, Player player) {
        List<LeagueStatProjection> leagueStats = tournamentResultRepository.getLeagueStats(player);
        sb.append("\n=== ПО ЛИГАМ ===\n");
        for (LeagueStatProjection ls : leagueStats) {
            sb.append(String.format("- Лига %s: %.0f руб, %d турниров, средний %.0f руб\n",
                    ls.getLeague(), ls.getSum(), ls.getCount(), ls.getAvg()));
        }
    }

    private void appendTopWeek(StringBuilder sb, Player player) {
        TopWeekResponse topWeek = topWeekService.getTopWithPosition(player.getId());
        sb.append("\n=== ТОП НЕДЕЛИ ===\n");
        sb.append(String.format("- Позиция: %d, Доход: %.0f руб, Турниров: %d\n",
                topWeek.getPlayerPosition(), topWeek.getPlayerTotal(), topWeek.getPlayerTournaments()));
        if (topWeek.getTitle() != null) sb.append(String.format("- Звание: %s\n", topWeek.getTitle()));
        if (topWeek.getTop5() != null) {
            sb.append("- Топ-5:\n");
            for (int i = 0; i < topWeek.getTop5().size(); i++) {
                var tp = topWeek.getTop5().get(i);
                sb.append(String.format("  %d. %s: %.0f руб\n", i+1, tp.getName(), tp.getTotal()));
            }
        }
    }

    private void appendUpcomingLineups(StringBuilder sb, Player player, LocalDate today) {
        List<Lineup> lineups = lineupRepository.findByDateBetweenOrderByDateAscTimeAsc(
                today.plusDays(1), today.plusDays(2));
        if (!lineups.isEmpty()) {
            sb.append("\n=== СОСТАВЫ НА БЛИЖАЙШИЕ ДНИ ===\n");
            for (Lineup l : lineups) {
                boolean in = l.getPlayers().toLowerCase().contains(player.getName().toLowerCase());
                sb.append(String.format("- %s %s Лига %s: %s %s\n",
                        l.getDate(), l.getTime(), l.getLeague(),
                        l.getPlayers(), in ? "(ты в составе)" : ""));
            }
        }
    }

    private void appendPrimaryLeague(StringBuilder sb, Player player) {
        sb.append(String.format("\n=== ОБЩЕЕ ===\nОсновная лига: %s\n",
                leagueService.getPrimaryLeague(player.getId())));
    }
}