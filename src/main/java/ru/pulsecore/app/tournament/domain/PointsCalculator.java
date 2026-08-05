// PointsCalculator.java
package ru.pulsecore.app.tournament.domain;

import java.time.LocalDate;

public interface PointsCalculator {
    int calculatePoints(Match match, LocalDate tournamentDate);
}