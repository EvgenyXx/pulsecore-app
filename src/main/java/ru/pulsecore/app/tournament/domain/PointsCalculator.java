package ru.pulsecore.app.tournament.domain;

import ru.pulsecore.app.tournament.domain.model.Match;

import java.time.LocalDate;

public interface PointsCalculator {
    int calculatePoints(Match match, LocalDate tournamentDate);
}