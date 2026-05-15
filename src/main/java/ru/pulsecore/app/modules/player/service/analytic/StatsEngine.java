package ru.pulsecore.app.modules.player.service.analytic;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.core.dto.StatProjection;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsEngine {

    private final TournamentResultService resultService;
    private final GigaChatService ai;
    private final ObjectMapper mapper = new ObjectMapper();

    public StatProjection calculate(Player player, String question) {
        LocalDate today = LocalDate.now();

        // Один запрос — одно определение дат. Без истории.
        String json = ai.analyze(String.format("""
            Определи период дат для вопроса. Сегодня: %s.
            
            Верни ТОЛЬКО JSON: {"start":"YYYY-MM-DD","end":"YYYY-MM-DD"}
            
            Вопрос: %s
            """, today, question));

        try {
            String clean = json.replaceAll("```json|```", "").trim();
            Map<String, String> map = mapper.readValue(clean, Map.class);
            LocalDate start = LocalDate.parse(map.get("start"));
            LocalDate end = LocalDate.parse(map.get("end"));

            PeriodStatsProjection stats = resultService.getStatsByPeriod(player, start, end);
            double sum = stats != null ? stats.getSum() : 0;
            long count = stats != null ? stats.getCount() : 0;

            return new StatProjection(humanLabel(start, end), sum, count, start, end);
        } catch (Exception e) {
            log.error("StatsEngine failed: {}", e.getMessage());
            LocalDate start = today.withDayOfMonth(1);
            PeriodStatsProjection stats = resultService.getStatsByPeriod(player, start, today);
            double sum = stats != null ? stats.getSum() : 0;
            long count = stats != null ? stats.getCount() : 0;
            return new StatProjection("текущий месяц", sum, count, start, today);
        }
    }

    private String humanLabel(LocalDate start, LocalDate end) {
        if (start.getDayOfMonth() == 1 && end.equals(start.plusMonths(1).minusDays(1))) {
            return monthName(start.getMonthValue()) + " " + start.getYear();
        }
        if (start.getDayOfMonth() == 1 && start.getMonthValue() == 1 &&
                end.getDayOfMonth() == 31 && end.getMonthValue() == 12) {
            return start.getYear() + " год";
        }
        return start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String monthName(int m) {
        String[] n = {"Январь","Февраль","Март","Апрель","Май","Июнь",
                "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};
        return n[m - 1];
    }
}