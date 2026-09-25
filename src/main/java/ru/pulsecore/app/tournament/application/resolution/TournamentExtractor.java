package ru.pulsecore.app.tournament.application.resolution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.application.calculation.league.NightBonusService;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
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

    /**
     * Обёртка: парсит Document ОДИН раз и делегирует в extract(page).
     * Используй её, только если у тебя на руках Document и нет TournamentPage.
     */
    public TournamentContext extract(Document doc) {
        TournamentPage page = jsonTournamentParser.parse(doc);
        return extract(page);
    }

    /**
     * Основной метод: работает с уже распарсенной страницей.
     * Никакого повторного парсинга — всё берётся из page.
     */
    public TournamentContext extract(TournamentPage page) {
        if (page == null) {
            log.warn("Не удалось распарсить страницу");
            return null;
        }

        Long tournamentId = page.id();
        String date = page.date();
        String time = page.time();
        String removed = page.removedPlayer();
        String typeId = page.typeId();

        TournamentStatus status = jsonTournamentStatusParser.parseStatus(page);
        List<Match> matches = jsonMatchParser.parseMatches(page);

        LeagueType league = leagueDetector.detectLeague(page);
        if (league == null) {
            log.warn("Не удалось определить лигу для турнира id={}", tournamentId);
            return null;
        }

        double nightBonus = nightBonusService.calculateBonus(page, league.name());

        RemovedResult playerDetector = removedPlayerDetector.detect(removed, matches);

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
                time,
                typeId
        );
    }
}