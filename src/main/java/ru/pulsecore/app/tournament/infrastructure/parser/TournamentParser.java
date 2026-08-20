package ru.pulsecore.app.tournament.infrastructure.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TournamentParser {




    public Long parseTournamentId(Document doc) {
        Element shortLink = doc.select(HtmlSelectors.SHORTLINK).first();

        if (shortLink == null) return null;

        String url = shortLink.attr("href");

        return Long.parseLong(url.replaceAll(".*p=(\\d+)", "$1"));
    }

    public String parseDate(Document doc) {
        Element dateElement = doc.select(HtmlSelectors.DATE).first();
        return dateElement != null ? dateElement.text() : null;
    }

    public String parseTime(Document doc) {
        Element timeElement = doc.select(HtmlSelectors.TIME).first();
        if (timeElement != null) {
            String time = timeElement.text().trim();
            if (time.matches("\\d{2}:\\d{2}")) {
                return time;
            }
        }
        return null;
    }

    // 🔥 НОВЫЙ МЕТОД: нормализует список всех игроков
    public List<String> parseAndNormalizePlayers(Document doc) {
        return doc.select(HtmlSelectors.PLAYER)
                .stream()
                .map(Element::text)
                .map(NameNormalizer::normalize)
                .collect(Collectors.toList());
    }

    // 🔥 ИСПРАВЛЕН: использует NameNormalizer вместо локального normalize
    public String findRemovedPlayer(Document doc) {
        return doc.select(HtmlSelectors.PLAYER)
                .stream()
                .filter(player -> player.hasClass(HtmlSelectors.STATUS_REMOVED))
                .map(Element::text)
                .map(NameNormalizer::normalize)
                .findFirst()
                .orElse(null);
    }
}