package ru.pulsecore.app.core.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.model.LeagueType;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeagueDetector {

    public LeagueType detectLeague(Document doc) {
        String title = doc.title();
        log.info("League detection: title='{}', html length={}", title, doc.html().length());

        LeagueType fromTitle = detectFromText(title);
        if (fromTitle != null) return fromTitle;

        String bodyText = doc.body().text();
        LeagueType fromBody = detectFromText(bodyText);
        if (fromBody != null) {
            log.info("League detected from body: {}", fromBody);
            return fromBody;
        }

        throw new LeagueDetectionException(title);
    }

    private LeagueType detectFromText(String text) {
        if (text.contains("Лига A") || text.contains("Лига А")
                || text.contains("Лига: A") || text.contains("Лига: А")) return LeagueType.A;
        if (text.contains("Лига В") || text.contains("Лига B")
                || text.contains("Лига: B") || text.contains("Лига: В")) return LeagueType.B;
        if (text.contains("Лига С") || text.contains("Лига C")
                || text.contains("Лига: C") || text.contains("Лига: С")) return LeagueType.C;
        if (text.contains("Лига D") || text.contains("Лига: D")) return LeagueType.D;
        if (text.contains("Мужская Суперлига") || text.contains("Женская Суперлига")
                || text.contains("Суперлига")) return LeagueType.SUPER_LEAGUE;
        return null;
    }
}