package ru.pulsecore.app.modules.player.service.analytic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.player.api.dto.TopWeekResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.api.dto.LeagueStatProjection;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversalAssistantService {

    private final SmartBuddyService ai;
    private final TournamentResultService resultService;
    private final PlayerStatsService statsService;
    private final TournamentResultRepository tournamentResultRepository;
    private final LineupRepository lineupRepository;

    private final Map<UUID, List<Map<String, String>>> dialogs = new HashMap<>();

    private static final String[] MONTHS = {
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    public String answer(Player player, String question) {
        List<Map<String, String>> history = dialogs.computeIfAbsent(
                player.getId(), k -> new ArrayList<>());

        String firstName = extractFirstName(player.getName());
        LocalDate today = LocalDate.now();

        String allData = gatherData(player, today);

        String system = String.format("""
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
            """, player.getName(), firstName, today);

        String userMsg = String.format("""
            ДАННЫЕ ИГРОКА:
            %s
            
            ВОПРОС: %s
            """, allData, question);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", system));
        messages.addAll(history);
        messages.add(Map.of("role", "user", "content", userMsg));

        String answer = ai.analyze(messages);
        answer = answer.replaceAll("\\*\\*", "").replace("__", "").replaceAll("\\*", "");

        history.add(Map.of("role", "user", "content", question));
        history.add(Map.of("role", "assistant", "content", answer));
        if (history.size() > 30) {
            history = new ArrayList<>(history.subList(history.size() - 30, history.size()));
        }
        dialogs.put(player.getId(), history);

        return answer;
    }

    private String gatherData(Player player, LocalDate today) {
        StringBuilder sb = new StringBuilder();

        // ========== ВСЕ ТУРНИРЫ (последние 200) ==========
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

        // ========== ТЕКУЩАЯ НЕДЕЛЯ ==========
        PeriodStatsProjection week = resultService.getStatsByPeriod(player, today.minusDays(6), today);
        sb.append(String.format("\n=== ТЕКУЩАЯ НЕДЕЛЯ ===\n%.0f руб, %d турниров\n",
                week != null ? week.getSum() : 0, week != null ? week.getCount() : 0));

        // ========== ПРОШЛАЯ НЕДЕЛЯ ==========
        PeriodStatsProjection prevWeek = resultService.getStatsByPeriod(player, today.minusDays(13), today.minusDays(7));
        sb.append(String.format("=== ПРОШЛАЯ НЕДЕЛЯ ===\n%.0f руб, %d турниров\n",
                prevWeek != null ? prevWeek.getSum() : 0, prevWeek != null ? prevWeek.getCount() : 0));

        // ========== ТЕКУЩИЙ МЕСЯЦ ==========
        PeriodStatsProjection curMonth = resultService.getStatsByPeriod(player, today.withDayOfMonth(1), today);
        sb.append(String.format("=== ТЕКУЩИЙ МЕСЯЦ ===\n%.0f руб, %d турниров\n",
                curMonth != null ? curMonth.getSum() : 0, curMonth != null ? curMonth.getCount() : 0));

        // ========== ПОМЕСЯЧНО 2025-2026 ==========
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

        // ========== ИТОГИ ПО ГОДАМ ==========
        sb.append("\n=== ИТОГИ ===\n");
        PeriodStatsProjection y2025 = resultService.getStatsByPeriod(player, LocalDate.of(2025,1,1), LocalDate.of(2025,12,31));
        sb.append(String.format("- 2025: %.0f руб, %d турниров\n",
                y2025 != null ? y2025.getSum() : 0, y2025 != null ? y2025.getCount() : 0));
        PeriodStatsProjection y2026 = resultService.getStatsByPeriod(player, LocalDate.of(2026,1,1), today);
        sb.append(String.format("- 2026: %.0f руб, %d турниров\n",
                y2026 != null ? y2026.getSum() : 0, y2026 != null ? y2026.getCount() : 0));

        // ========== СТАТИСТИКА ПО ЛИГАМ ==========
        List<LeagueStatProjection> leagueStats = tournamentResultRepository.getLeagueStats(player);
        sb.append("\n=== ПО ЛИГАМ ===\n");
        for (LeagueStatProjection ls : leagueStats) {
            sb.append(String.format("- Лига %s: %.0f руб, %d турниров, средний %.0f руб\n",
                    ls.getLeague(), ls.getSum(), ls.getCount(), ls.getAvg()));
        }

        // ========== ТОП НЕДЕЛИ ==========
        TopWeekResponse topWeek = statsService.getTopWithPosition(player.getId());
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

        // ========== СОСТАВЫ ==========
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

        // ========== ОБЩАЯ ИНФА ==========
        sb.append(String.format("\n=== ОБЩЕЕ ===\nОсновная лига: %s\n",
                statsService.getPrimaryLeague(player.getId())));

        return sb.toString();
    }

    public String analyzeWeeklyComparison(Player player) {
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

        String league = statsService.getPrimaryLeague(player.getId());
        int position = statsService.getTopWithPosition(player.getId()).getPlayerPosition();

        String prompt = String.format("""
            Проанализируй недельную статистику игрока %s.
            
            Текущая неделя: %.0f руб, %d турниров, средний чек %.0f руб
            Прошлая неделя: %.0f руб, %d турниров, средний чек %.0f руб
            Основная лига: %s
            Позиция в топе: %d
            
            Дай короткий анализ (2-3 предложения): сравни недели, тренд среднего чека, один совет.
            Отвечай на "ты", без markdown, с цифрами.
            """, firstName, thisSum, thisCount, thisAvg,
                lastSum, lastCount, lastAvg, league, position);

        return ai.analyze(prompt).replaceAll("\\*\\*", "").replace("__", "").replaceAll("\\*", "");
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        String name = parts.length >= 2 ? parts[1] : parts[0];
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}