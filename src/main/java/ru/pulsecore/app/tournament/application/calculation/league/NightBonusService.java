package ru.pulsecore.app.tournament.application.calculation.league;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.infrastructure.util.RegionTimeUtils;

import java.time.LocalTime;

@Slf4j
@Service
public class NightBonusService {

    public double getBonus(String league) {
        return switch (league) {
            case "A" -> 1000;
            case "B" -> 750;
            case "C" -> 500;
            case "D" -> 200;
            default -> 0;
        };
    }

    public double calculateBonus(TournamentPage page, String league) {
        if (page == null) return 0;

        LocalTime time = parseTime(page.time());
        int hall = parseHall(page.hall());

        if (time == null) {
            log.warn("Ночной бонус: нет времени (url={}), бонус = 0",
                    page.document() != null ? page.document().baseUri() : "null");
            return 0;
        }

        boolean night = RegionTimeUtils.isNight(time, hall);
        log.debug("🕐 Расчёт ночного бонуса: время={}, зал={}, лига={}, ночной={}",
                time, hall, league, night);

        return night ? getBonus(league) : 0;
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            log.warn("Не удалось распарсить время: {}", timeStr);
            return null;
        }
    }

    private int parseHall(String hallStr) {
        if (hallStr == null || hallStr.isBlank()) return 0;
        try {
            return Integer.parseInt(hallStr.replaceAll("\\D", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}