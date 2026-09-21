package ru.pulsecore.app.tournament.infrastructure.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeagueDetector {

    public LeagueType detectLeague(Document doc) {
        String title = doc.title();


        LeagueType fromTitle = detectFromText(title);
        if (fromTitle != null) return fromTitle;

        String bodyText = doc.body().text();
        LeagueType fromBody = detectFromText(bodyText);
        if (fromBody != null) {
            log.info("League detected from body: {}", fromBody);
            return fromBody;
        }

        log.warn("Could not detect league: '{}'", title);
        return null;
    }



    private LeagueType detectFromText(String text) {
        String normalized = text.toLowerCase().replaceAll("\\s+", "");

        if (normalized.contains("лигаa") || normalized.contains("лигаа")) return LeagueType.A;
        if (normalized.contains("лигаb") || normalized.contains("лигав")) return LeagueType.B;
        if (normalized.contains("лигаc") || normalized.contains("лигас")) return LeagueType.C;
        if (normalized.contains("лигаd")) return LeagueType.D;
        if (normalized.contains("суперлига")) return LeagueType.SUPER_LEAGUE;
        return null;
    }

}