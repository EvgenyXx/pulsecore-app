package ru.pulsecore.app.tournament.application.compare;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.api.dto.response.SalaryH2HPlayerDto;

@Component
public class SalaryH2HCalculator {

    private static final double WEIGHT_EFFICIENCY = 0.50; // КПД (Сила лиги)
    private static final double WEIGHT_WINRATE = 0.40;    // Винрейт
    private static final double WEIGHT_ACTIVITY = 0.10;   // Турниры

    public String determineWinner(SalaryH2HPlayerDto p1, SalaryH2HPlayerDto p2) {
        if (p1.getTopsisScore() > p2.getTopsisScore()) {
            return p1.getName();
        } else if (p2.getTopsisScore() > p1.getTopsisScore()) {
            return p2.getName();
        }
        return "Ничья";
    }

    public void calculateScores(SalaryH2HPlayerDto p1, SalaryH2HPlayerDto p2, double leagueAvg1, double leagueAvg2) {
        // 1. Считаем КПД (Средний чек / Средняя по лиге)
        double kd1 = leagueAvg1 > 0 ? p1.getAvgAmount() / leagueAvg1 : 1.0;
        double kd2 = leagueAvg2 > 0 ? p2.getAvgAmount() / leagueAvg2 : 1.0;

        // 2. Массивы значений [КПД, Винрейт, Турниры]
        double[] v1 = {kd1, p1.getWinratePercent(), p1.getTournamentsCount()};
        double[] v2 = {kd2, p2.getWinratePercent(), p2.getTournamentsCount()};
        double[] weights = {WEIGHT_EFFICIENCY, WEIGHT_WINRATE, WEIGHT_ACTIVITY};

        // 3. Считаем полезность (Utility)
        double utility1 = 0.0;
        double utility2 = 0.0;

        for (int i = 0; i < 3; i++) {
            double max = Math.max(v1[i], v2[i]);
            double min = Math.min(v1[i], v2[i]);

            if (max == 0) {
                utility1 += 0.5 * weights[i];
                utility2 += 0.5 * weights[i];
                continue;
            }

            if (v1[i] == max) {
                utility1 += 1.0 * weights[i];
                utility2 += (min / max) * weights[i];
            } else {
                utility1 += (min / max) * weights[i];
                utility2 += 1.0 * weights[i];
            }
        }

        // 4. Переводим в проценты
        double total = utility1 + utility2;
        double score1 = total > 0 ? (utility1 / total) * 100 : 50.0;
        double score2 = total > 0 ? (utility2 / total) * 100 : 50.0;

        // 5. Округляем и сохраняем
        p1.setTopsisScore(round(score1, 1));
        p2.setTopsisScore(round(score2, 1));
    }

    private double round(double value, int places) {
        long factor = (long) Math.pow(10, places);
        return (double) Math.round(value * factor) / factor;
    }


}