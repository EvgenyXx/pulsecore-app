package ru.pulsecore.app.core.stats;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.model.Match;

import java.time.LocalDate;

@Service
public class LeagueCPointsCalculator implements PointsCalculator {

    private static final LocalDate NEW_MODEL_DATE = LocalDate.of(2026, 3, 3);

    @Override
    public int calculatePoints(Match match, LocalDate tournamentDate) {
        if (tournamentDate != null && tournamentDate.isBefore(NEW_MODEL_DATE)) {
            return calculateOld(match);
        }
        return calculateNew(match);
    }

    private int calculateOld(Match match) {
        int a = match.getScore1();
        if (a == 4) return 800;
        if (a == 3) return 500;
        if (a == 2) return 400;
        if (a == 1) return 300;
        return 200;
    }

    private int calculateNew(Match match) {
        int a = match.getScore1();
        if (a == 4) return 900;
        if (a == 3) return 500;
        if (a == 2) return 400;
        if (a == 1) return 300;
        return 200;
    }
}