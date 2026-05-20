// ==================== PointsCalculatorUtils.java ====================
package ru.pulsecore.app.core.stats;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;

public final class PointsCalculatorUtils {

    private static final Set<MonthDay> DOUBLE_DAYS = Set.of(
            MonthDay.of(12, 31), // Новогодняя ночь
            MonthDay.of(1, 1)     // Новый год
    );

    private PointsCalculatorUtils() {}

    public static int applyDoubleBonus(int points, LocalDate date) {
        if (date != null && DOUBLE_DAYS.contains(MonthDay.from(date))) {
            return points * 2;
        }
        return points;
    }
}