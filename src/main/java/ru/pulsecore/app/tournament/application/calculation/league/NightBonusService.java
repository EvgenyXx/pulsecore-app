package ru.pulsecore.app.tournament.application.calculation.league;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.parser.HtmlSelectors;
import ru.pulsecore.app.tournament.infrastructure.util.HtmlParserUtil;
import ru.pulsecore.app.tournament.infrastructure.util.RegionTimeUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

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

    public double calculateBonus(Document doc, String league) {
        LocalTime time = HtmlParserUtil.parseTime(HtmlParserUtil.extractTime(doc));
        int hall = HtmlParserUtil.extractHallNumber(doc);

        log.debug("🕐 Расчёт ночного бонуса: время={}, зал={}, лига={}, ночной={}",
                time, hall, league, RegionTimeUtils.isNight(time, hall));
        return RegionTimeUtils.isNight(time, hall) ? getBonus(league) : 0;
    }


}