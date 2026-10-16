package ru.pulsecore.app.tournament.application.resolution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.api.dto.TournamentJson;
import ru.pulsecore.app.tournament.application.calculation.WithdrawalDetector;
import ru.pulsecore.app.tournament.application.calculation.league.NightBonusService;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.enums.WithdrawalStage;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.RemovedResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonMatchParser;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentParser;
import ru.pulsecore.app.tournament.infrastructure.parser.JsonTournamentStatusParser;
import ru.pulsecore.app.tournament.infrastructure.parser.league.LeagueDetector;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentExtractor {

    private final JsonTournamentParser jsonTournamentParser;
    private final JsonMatchParser jsonMatchParser;
    private final JsonTournamentStatusParser jsonTournamentStatusParser;

    private final LeagueDetector leagueDetector;
    private final NightBonusService nightBonusService;
    private final RemovedPlayerDetector removedPlayerDetector;
    private final WithdrawalDetector withdrawalDetector;

    /**
     * Обёртка: парсит Document ОДИН раз и делегирует в extract(page).
     */
    public TournamentContext extract(Document doc) {
        TournamentPage page = jsonTournamentParser.parse(doc);
        return extract(page);
    }

    /**
     * Основной метод: работает с уже распарсенной страницей.
     */
    public TournamentContext extract(TournamentPage page) {
        if (page == null) {
            log.warn("Не удалось распарсить страницу");
            return null;
        }

        Long tournamentId = page.json().tourId();
        String date = page.json().date();
        String time = page.json().time();
        String removed = findRemovedPlayer(page.json());
        String typeId = page.json().typeId();

        TournamentStatus status = jsonTournamentStatusParser.parseStatus(page);
        List<Match> matches = jsonMatchParser.parseMatches(page);

        LeagueType league = leagueDetector.detectLeague(page);
        if (league == null) {
            log.warn("Не удалось определить лигу для турнира id={}", tournamentId);
            return null;
        }

        double nightBonus = nightBonusService.calculateBonus(page, league.name());

        RemovedResult playerDetector = removedPlayerDetector.detect(removed, matches);
        WithdrawalStage stage = withdrawalDetector.detect(matches);

        TournamentContext ctx = new TournamentContext(
                tournamentId,
                status,
                date,
                matches,
                league,
                nightBonus,
                playerDetector.stage(),
                playerDetector.player(),
                time,
                typeId,
                stage
        );



        log.debug("Extract: id={}, date={}, time={}, league={}, matches={}, bonus={}, status={}, withdrawal={}",
                tournamentId, date, time, league, matches.size(), nightBonus, status, ctx.getWithdrawalStage());

        return ctx;
    }

    private String findRemovedPlayer(TournamentJson json) {
        return json.players().stream()
                .filter(p -> Boolean.TRUE.equals(p.removed()))
                .map(TournamentJson.PlayerJson::name)
                .findFirst()
                .orElse(null);
    }
}