package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.ResultDto;
import ru.pulsecore.app.core.integration.DocumentLoader;
import ru.pulsecore.app.core.stats.PointsCalculatorUtils;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;
import ru.pulsecore.app.modules.tournament.calculation.ResultBuilder;
import ru.pulsecore.app.modules.tournament.calculation.strategy.MatchCalculationStrategy;
import ru.pulsecore.app.modules.tournament.calculation.strategy.StrategyResolver;
import ru.pulsecore.app.modules.tournament.calculation.strategy.removed.RemovedStage;
import ru.pulsecore.app.modules.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentContext;
import ru.pulsecore.app.modules.tournament.extraction.TournamentExtractor;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultService {

    private final DocumentLoader loader;
    private final TournamentExtractor tournamentExtractor;
    private final StrategyResolver strategyResolver;
    private final ResultBuilder resultBuilder;
    private final NameNormalizer nameNormalizer;

    public ParsedResult calculateAll(String url) {
        Document doc = loader.load(url);
        return calculate(doc);
    }

    public ParsedResult calculateAll(Document doc) {
        return calculate(doc);
    }

    private ParsedResult calculate(Document doc) {
        TournamentContext ctx = tournamentExtractor.extract(doc);
        List<ResultDto> results = buildResults(ctx);
        normalizeNames(results);
        applyBonusPoints(ctx, results);
        results.sort((a, b) -> Integer.compare(b.getTotal(), a.getTotal()));
        logResults(ctx, results);

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
            String normalizedName = nameNormalizer.normalize(result.getPlayer());
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

    private void logResults(TournamentContext ctx, List<ResultDto> results) {
        if (results.isEmpty() && ctx.getTournamentStatus() != null) {
            log.info("Tournament {}: no results (status={})", ctx.getTournamentId(), ctx.getTournamentStatus());
        } else {
            log.info("Tournament {}: {} results, status={}", ctx.getTournamentId(), results.size(), ctx.getTournamentStatus());
        }
    }

    private boolean hasRemoved(TournamentContext ctx) {
        return ctx.getRemovedStage() != null && ctx.getRemovedStage() != RemovedStage.NONE;
    }

    private boolean isFinalRemoved(TournamentContext ctx) {
        return ctx.getRemovedStage() == RemovedStage.FINAL;
    }
}