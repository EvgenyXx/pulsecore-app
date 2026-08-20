package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.time.LocalDate;

@UtilityClass
public class TournamentDateUtils {

    public LocalDate extractDate(TournamentDto t) {
        try {
            return LocalDate.parse(t.getDate().getDate().substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    public String extractTime(TournamentDto t) {
        try {
            return t.getDate().getDate().substring(11, 16);
        } catch (Exception e) {
            return "??:??";
        }
    }
}