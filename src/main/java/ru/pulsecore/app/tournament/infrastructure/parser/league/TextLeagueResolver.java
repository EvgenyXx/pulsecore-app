package ru.pulsecore.app.tournament.infrastructure.parser.league;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

@Slf4j
@Component
public class TextLeagueResolver implements LeagueResolver {

    @Override
    public LeagueType resolve(TournamentPage page) {
        if (page == null || page.document() == null) return null;

        Document doc = page.document();

        LeagueType fromTitle = detectFromText(doc.title());
        if (fromTitle != null) {
            log.debug("League detected from title: {}", fromTitle);
            return fromTitle;
        }

        String bodyText = doc.body().text();
        LeagueType fromBody = detectFromText(bodyText);
        if (fromBody != null) {
            log.info("League detected from body: {}", fromBody);
        }
        return fromBody;
    }

    @Override
    public int order() {
        return 10;
    }

    private LeagueType detectFromText(String text) {
        if (text == null) return null;

        String normalized = text.toLowerCase().replaceAll("\\s+", "");

        if (normalized.contains("лигаa") || normalized.contains("лигаа")) return LeagueType.A;
        if (normalized.contains("лигаb") || normalized.contains("лигав")) return LeagueType.B;
        if (normalized.contains("лигаc") || normalized.contains("лигас")) return LeagueType.C;
        if (normalized.contains("лигаd")) return LeagueType.D;
        if (normalized.contains("суперлига")) return LeagueType.SUPER_LEAGUE;
        return null;
    }
}