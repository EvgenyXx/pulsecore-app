package ru.pulsecore.app.modules.tournament.application.tournament.extraction;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.model.LeagueType;
import ru.pulsecore.app.core.model.Match;
import ru.pulsecore.app.core.parser.LeagueDetector;
import ru.pulsecore.app.core.stats.NightBonusService;
import ru.pulsecore.app.modules.notification.infrastructure.properties.AdminProperties;
import ru.pulsecore.app.modules.notification.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.notification.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification.application.mail.context.BrokenUriContext;

import ru.pulsecore.app.modules.tournament.domain.RemovedResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentContext;
import ru.pulsecore.app.modules.tournament.domain.TournamentStatus;
import ru.pulsecore.app.modules.tournament.infrastructure.parser.MatchParser;
import ru.pulsecore.app.modules.tournament.infrastructure.parser.TournamentParser;
import ru.pulsecore.app.modules.tournament.infrastructure.parser.TournamentStatusParser;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TournamentExtractor {

    private final TournamentParser tournamentParser;
    private final MatchParser matchParser;
    private final LeagueDetector leagueDetector;
    private final NightBonusService nightBonusService;
    private final TournamentStatusParser tournamentStatusParser;
    private final RemovedPlayerDetector removedPlayerDetector;
    private final MailStrategyRegistry mailStrategyRegistry;
    private final AdminProperties adminProperties;

    public TournamentContext extract(Document doc) {

        Long tournamentId = tournamentParser.parseTournamentId(doc);
        TournamentStatus status = tournamentStatusParser.parseStatus(doc);
        String date = tournamentParser.parseDate(doc);

        List<Match> matches = matchParser.parseMatches(doc);

        LeagueType league = leagueDetector.detectLeague(doc);
        if (league == null){
            String brokenUri = doc.baseUri();
            mailStrategyRegistry.send(
                    MailTypes.BROKEN_URI,
                    new BrokenUriContext(adminProperties.getEmail(),brokenUri)
            );
            return null;
        }

        double nightBonus = nightBonusService.calculateBonus(doc, league.name());

        String removedPlayer = tournamentParser.findRemovedPlayer(doc);
        String time = tournamentParser.parseTime(doc);


        RemovedResult playerDetector = removedPlayerDetector.detect(removedPlayer,matches);



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