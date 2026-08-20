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
//        int hall = Integer.parseInt(HtmlParserUtil.extractHall(doc).replaceAll("\\D+", ""));
        int hall = HtmlParserUtil.extractHallNumber(doc);

        log.info("🕐 Расчёт ночного бонуса: время={}, зал={}, лига={}, ночной={}",
                time, hall, league, RegionTimeUtils.isNight(time, hall));
        return RegionTimeUtils.isNight(time, hall) ? getBonus(league) : 0;
    }

//    private static final LocalTime NIGHT_BORDER = LocalTime.of(6, 0);
//    private static final ZoneId MSK = ZoneId.of("Europe/Moscow");
//    private static final ZoneId VLADIVOSTOK = ZoneId.of("Asia/Vladivostok");
//    private static final int USSURIYSK_HALL = 5;
//
//    public boolean isNight(Document doc) {
//        String time = extractTime(doc);
//        LocalTime startTime = parseTime(time);
//
//        if (isUssuriyskHall(doc)) {//todo вынести в отдельный класс который занимается временем для всего этого
//            startTime = startTime.atDate(LocalDate.now())
//                    .atZone(MSK)
//                    .withZoneSameInstant(VLADIVOSTOK)
//                    .toLocalTime();
//        }
//
//        return startTime.isBefore(NIGHT_BORDER); //старт тайм (7) это раньше отрезка 0-6
//    }

//    public double getBonus(String league) {
//        return switch (league) {
//            case "A" -> 1000;
//            case "B" -> 750;
//            case "C" -> 500;
//            case "D" -> 200;
//            default -> 0;
//        };
//    }

//    public double calculateBonus(Document doc, String league) {
//        return isNight(doc) ? getBonus(league) : 0;
//    }
//
//    private boolean isUssuriyskHall(Document doc) {
//        String hall = extractHall(doc);
//        String digits = hall.replaceAll("\\D+", "");
//        return String.valueOf(USSURIYSK_HALL).equals(digits);
//    }
//
//    private String extractHall(Document doc) {
//        return doc.select(HtmlSelectors.TABLE)
//                .text()
//                .trim();
//    }
//
//    private String extractTime(Document doc) {
//        return doc.select(HtmlSelectors.TIME_ROW_SELECTOR)
//                .stream()
//                .filter(el -> el.text().contains(HtmlSelectors.TIME_LABEL))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Не найдено время турнира"))
//                .select(HtmlSelectors.TD_SELECTOR)
//                .text();
//    }
//
//    private LocalTime parseTime(String rawTime) {
//        String normalized = rawTime.length() > 5
//                ? rawTime.substring(0, 5)
//                : rawTime;
//
//        return LocalTime.parse(normalized);
//    }
}