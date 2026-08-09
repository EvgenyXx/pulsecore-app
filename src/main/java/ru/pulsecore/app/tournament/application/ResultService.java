package ru.pulsecore.app.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.application.calculation.ResultBuilder;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.infrastructure.parser.DocumentLoader;
import ru.pulsecore.app.tournament.domain.model.PointsCalculatorUtils;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.application.calculation.StrategyResolver;
import ru.pulsecore.app.tournament.domain.enums.RemovedStage;
import ru.pulsecore.app.tournament.domain.model.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import ru.pulsecore.app.tournament.application.extraction.TournamentExtractor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultService {

    private final DocumentLoader loader;
    private final TournamentExtractor tournamentExtractor;
    private final StrategyResolver strategyResolver;
    private final ResultBuilder resultBuilder;


    public ParsedResult calculateAll(String url) {
        Document doc = loader.load(url);
        return calculate(doc);
    }

    public ParsedResult calculateAll(Document doc) {
        return calculate(doc);
    }

    private ParsedResult calculate(Document doc) {
        TournamentContext ctx = tournamentExtractor.extract(doc);

        if (ctx == null){
            return null;
        }
        List<ResultDto> results = buildResults(ctx);
        normalizeNames(results);
        applyBonusPoints(ctx, results);
        results.sort((a, b) -> Integer.compare(b.getTotal(), a.getTotal()));
        logResults(ctx, results,doc.baseUri());

        return new ParsedResult(
                ctx.getTournamentId(),
                results,
                ctx.getTournamentStatus(),
                ctx.getNightBonus(),
                hasRemoved(ctx),
                isFinalRemoved(ctx),
                ctx.getLeague().name(),
                ctx.getTime()
        );
    }

    private List<ResultDto> buildResults(TournamentContext ctx) {
        MatchCalculationStrategy strategy = strategyResolver.resolve(ctx);
        MatchProcessingResult matchResult = strategy.process(ctx);
        return resultBuilder.build(matchResult, ctx);
    }

    private void normalizeNames(List<ResultDto> results) {
        for (ResultDto result : results) {
            String normalizedName = NameNormalizer.normalize(result.getPlayer());
            result.setPlayer(normalizedName);
        }
    }

    private void applyBonusPoints(TournamentContext ctx, List<ResultDto> results) {
        String dateStr = ctx.getDate();
        if (dateStr == null || dateStr.isEmpty()) return;
        LocalDate tournamentDate = LocalDate.parse(dateStr);

        for (ResultDto result : results) {
            int total = PointsCalculatorUtils.applyDoubleBonus(result.getTotal(), tournamentDate);
            result.setTotal(total);
        }
    }

    private void logResults(TournamentContext ctx, List<ResultDto> results,String url) {
        if (ctx.getTournamentStatus() != TournamentStatus.FINISHED) {
            String players = results.stream()
                    .map(ResultDto::getPlayer)
                    .collect(Collectors.joining(", "));
            log.info("Турнир: дата={}, время={}, лига={}, статус={},участники:{}, ссылка={}",
                    ctx.getDate(),
                    ctx.getTime() != null ? ctx.getTime() : "?",
                    ctx.getLeague(),
                    ctx.getTournamentStatus(),
                    players,
                    url

            );
        }

    }

    private boolean hasRemoved(TournamentContext ctx) {
        return ctx.getRemovedStage() != null && ctx.getRemovedStage() != RemovedStage.NONE;
    }

    private boolean isFinalRemoved(TournamentContext ctx) {
        return ctx.getRemovedStage() == RemovedStage.FINAL;
    }
}