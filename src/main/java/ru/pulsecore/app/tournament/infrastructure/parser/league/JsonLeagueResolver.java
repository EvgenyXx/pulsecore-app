package ru.pulsecore.app.tournament.infrastructure.parser.league;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.util.RegexUtils;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

@Slf4j
@Component
public class JsonLeagueResolver implements LeagueResolver {

    @Override
    public LeagueType resolve(TournamentPage page) {
        if (page == null) return null;


        String league = page.json().leagueTitle();
        if (league == null || league.isBlank()) return null;

        LeagueType result = fromLetter(league.trim().toUpperCase());
        if (result != null) {
            log.debug("League detected from JSON: '{}' -> {}", league, result);
        } else {
            log.warn("Unknown leagueTitle in JSON: '{}'", league);
        }
        return result;
    }

    @Override
    public int order() {
        return 5;
    }

    private LeagueType fromLetter(String letter) {
        String clean = RegexUtils.ONLY_LETTERS.matcher(letter).replaceAll("").toUpperCase();
        return switch (clean) {
            case "А", "A" -> LeagueType.A;
            case "В", "B" -> LeagueType.B;
            case "С", "C" -> LeagueType.C;
            case "D" -> LeagueType.D;
            case "СУПЕРЛИГА", "SUPER", "SUPERLEAGUE" -> LeagueType.SUPER_LEAGUE;
            default -> {
                log.warn("Неизвестная лига: '{}' (очищено: '{}')", letter, clean);
                yield null;
            }
        };
    }
}