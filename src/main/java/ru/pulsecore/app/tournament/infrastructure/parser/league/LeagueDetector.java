package ru.pulsecore.app.tournament.infrastructure.parser.league;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentParser;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeagueDetector {

     /**
     * Spring автоматически соберёт сюда ВСЕ бины, реализующие LeagueResolver.
     * Сейчас это JsonLeagueResolver (order=10) и TextLeagueResolver (order=100).
     */
    private final List<LeagueResolver> resolvers;

    public LeagueType detectLeague(TournamentPage page) {
        if (page == null) return null;

        List<LeagueResolver> ordered = resolvers.stream()
                .sorted(Comparator.comparingInt(LeagueResolver::order))
                .toList();

        LeagueResolver primary = ordered.isEmpty() ? null : ordered.get(0);

        for (LeagueResolver resolver : ordered) {
            try {
                LeagueType result = resolver.resolve(page);
                if (result == null) continue;

                if (primary != null && resolver != primary) {
                    log.warn("League detected by fallback resolver {}: {}",
                            resolver.getClass().getSimpleName(), result);
                }
                return result;
            } catch (Exception e) {
                log.warn("LeagueResolver {} failed: {}",
                        resolver.getClass().getSimpleName(), e.getMessage());
            }
        }

        log.warn("Could not detect league from any resolver");
        return null;
    }

//    private final JsonTournamentParser jsonTournamentParser;
//
//    public LeagueType detectLeague(Document doc) {
//
////        String title = doc.title();
//
//
////        LeagueType fromTitle = detectFromText(title);
//        LeagueType fromTitle = delectedFromJson(doc);
//        if (fromTitle != null) return fromTitle;
//
//        String bodyText = doc.body().text();
//        LeagueType fromBody = detectFromText(bodyText);
//        if (fromBody != null) {
//            log.info("League detected from body: {}", fromBody);
//            return fromBody;
//        }
//
//
////        log.warn("Could not detect league: '{}'", title);
//        return null;
//    }
//
//    private LeagueType delectedFromJson(Document document) {
//        String league = jsonTournamentParser.parseLeague(document);
//        System.err.println("ЛИГА НОВАЯ ИЗ ДЖИОНА " + league);
//
//        if (league == null) return null;
//        return fromJsonLetter(league.trim().toUpperCase());
//    }
//
//    private LeagueType fromJsonLetter(String letter) {
//        return switch (letter) {
//            case "A" -> LeagueType.A;
//            case "B" -> LeagueType.B;
//            case "C" -> LeagueType.C;
//            case "D" -> LeagueType.D;
//            case "СУПЕРЛИГА", "SUPER", "SUPER_LEAGUE", "SUPERLEAGUE" -> LeagueType.SUPER_LEAGUE;
//            default -> null;
//        };
//    }
//
//
//    private LeagueType detectFromText(String text) {
//        String normalized = text.toLowerCase().replaceAll("\\s+", "");
//
//        if (normalized.contains("лигаa") || normalized.contains("лигаа")) return LeagueType.A;
//        if (normalized.contains("лигаb") || normalized.contains("лигав")) return LeagueType.B;
//        if (normalized.contains("лигаc") || normalized.contains("лигас")) return LeagueType.C;
//        if (normalized.contains("лигаd")) return LeagueType.D;
//        if (normalized.contains("суперлига")) return LeagueType.SUPER_LEAGUE;
//        return null;
//    }

}