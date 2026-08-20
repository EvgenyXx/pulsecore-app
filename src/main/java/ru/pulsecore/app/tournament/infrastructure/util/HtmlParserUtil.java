package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import ru.pulsecore.app.tournament.infrastructure.parser.HtmlSelectors;

import java.time.LocalTime;


@UtilityClass
public class HtmlParserUtil {

    public String extractHall(Document doc) {
        return doc.select(HtmlSelectors.TABLE)
                .text()
                .trim();
    }

    public Integer extractHallNumber(Document doc) {
        String hall = extractHall(doc);
        String digits = hall.replaceAll("\\D+", "");
        return digits.isEmpty() ? null : Integer.parseInt(digits);
    }

    public String extractTime(Document doc) {
        return doc.select(HtmlSelectors.TIME_ROW_SELECTOR)
                .stream()
                .filter(el -> el.text().contains(HtmlSelectors.TIME_LABEL))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Не найдено время турнира"))
                .select(HtmlSelectors.TD_SELECTOR)
                .text();
    }

    public static LocalTime parseTime(String rawTime) {
        String normalized = rawTime.length() > 5
                ? rawTime.substring(0, 5)
                : rawTime;
        return LocalTime.parse(normalized);
    }
}