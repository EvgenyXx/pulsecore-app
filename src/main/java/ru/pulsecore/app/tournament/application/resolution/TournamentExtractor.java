package ru.pulsecore.app.tournament.application.resolution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.infrastructure.parser.LeagueDetector;
import ru.pulsecore.app.tournament.application.calculation.league.NightBonusService;
import ru.pulsecore.app.tournament.domain.model.RemovedResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonMatchParser;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentParser;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentStatusParser;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentExtractor {

    // старые — не трогаем, оставлены для обратной совместимости
    // private final TournamentParser tournamentParser;
    // private final MatchParser matchParser;
    // private final TournamentStatusParser tournamentStatusParser;

    // новые — читают JSON из <script id="ml-tour-bootstrap">
    private final JsonTournamentParser jsonTournamentParser;
    private final JsonMatchParser jsonMatchParser;
    private final JsonTournamentStatusParser jsonTournamentStatusParser;

    // не зависят от формата — работают и с HTML, и с JSON
    private final LeagueDetector leagueDetector;
    private final NightBonusService nightBonusService;
    private final RemovedPlayerDetector removedPlayerDetector;

    public TournamentContext extract(Document doc) {

        Long tournamentId = jsonTournamentParser.parseTournamentId(doc);
        TournamentStatus status = jsonTournamentStatusParser.parseStatus(doc);
        String date = jsonTournamentParser.parseDate(doc);

        List<Match> matches = jsonMatchParser.parseMatches(doc);

        LeagueType league = leagueDetector.detectLeague(doc);

        double nightBonus = nightBonusService.calculateBonus(doc, league.name());

        String removedPlayer = jsonTournamentParser.findRemovedPlayer(doc);
        String time = jsonTournamentParser.parseTime(doc);

        RemovedResult playerDetector = removedPlayerDetector.detect(removedPlayer, matches);

        log.debug("Extract: id={}, date={}, time={}, league={}, matches={}, bonus={}, status={}",
                tournamentId, date, time, league, matches.size(), nightBonus, status);

        return new TournamentContext(
                tournamentId,
                status,
                date,
                matches,
                league,
                nightBonus,
                playerDetector.stage(),
                playerDetector.player(),
                time
        );
    }
}