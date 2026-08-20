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
import ru.pulsecore.app.tournament.infrastructure.parser.MatchParser;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentParser;
import ru.pulsecore.app.tournament.infrastructure.parser.TournamentStatusParser;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentExtractor {

    private final TournamentParser tournamentParser;
    private final MatchParser matchParser;
    private final LeagueDetector leagueDetector;
    private final NightBonusService nightBonusService;
    private final TournamentStatusParser tournamentStatusParser;
    private final RemovedPlayerDetector removedPlayerDetector;
    private final BrokenUriService brokenUriService;


    public TournamentContext extract(Document doc) {

        Long tournamentId = tournamentParser.parseTournamentId(doc);
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);
        String date = tournamentParser.parseDate(doc);

        List<Match> matches = matchParser.parseMatches(doc);

        LeagueType league = leagueDetector.detectLeague(doc);
//        if (league == null) {
//            String brokenUri = doc.baseUri();
//            brokenUriService.handle(brokenUri);
//            return null;
//        }

        double nightBonus = nightBonusService.calculateBonus(doc, league.name());

        String removedPlayer = tournamentParser.findRemovedPlayer(doc);
        String time = tournamentParser.parseTime(doc);


        RemovedResult playerDetector = removedPlayerDetector.detect(removedPlayer, matches);


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