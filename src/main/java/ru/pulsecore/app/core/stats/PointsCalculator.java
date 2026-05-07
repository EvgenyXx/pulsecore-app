// PointsCalculator.java
package ru.pulsecore.app.core.stats;

import ru.pulsecore.app.core.model.Match;
import java.time.LocalDate;

public interface PointsCalculator {
    int calculatePoints(Match match, LocalDate tournamentDate);
}