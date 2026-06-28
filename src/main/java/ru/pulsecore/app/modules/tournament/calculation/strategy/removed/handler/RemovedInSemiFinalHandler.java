package ru.pulsecore.app.modules.tournament.calculation.strategy.removed.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.model.Match;
import ru.pulsecore.app.modules.shared.util.StringUtils;
import ru.pulsecore.app.modules.tournament.calculation.MatchStage;
import ru.pulsecore.app.modules.tournament.calculation.strategy.DefaultMatchCalculationStrategy;
import ru.pulsecore.app.modules.tournament.calculation.strategy.removed.RemovedPlayerHandler;
import ru.pulsecore.app.modules.tournament.calculation.strategy.removed.RemovedStage;
import ru.pulsecore.app.modules.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.modules.tournament.domain.TournamentContext;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemovedInSemiFinalHandler implements RemovedPlayerHandler {

    private final DefaultMatchCalculationStrategy defaultStrategy;

    @Override
    public RemovedStage getStage() {
        return RemovedStage.SEMI_FINAL;
    }

    @Override
    public MatchProcessingResult handle(TournamentContext ctx) {
        MatchProcessingResult result = defaultStrategy.process(ctx);

        List<Match> semis = ctx.getMatches().stream()
                .filter(m -> MatchStage.SEMI_FINAL.matches(m.getStage()))
                .toList();

        if (semis.size() < 2) return result;

        Match finalMatch = ctx.getMatches().stream()
                .filter(m -> MatchStage.FINAL.matches(m.getStage()))
                .findFirst()
                .orElse(null);

        if (finalMatch == null) return result;

        Match canceledSemi = semis.stream()
                .filter(this::isCanceled)
                .findFirst()
                .orElse(null);

        if (canceledSemi == null) return result;

        String f1 = StringUtils.normalizeSearch(finalMatch.getPlayer1());
        String f2 = StringUtils.normalizeSearch(finalMatch.getPlayer2());
        String c1 = StringUtils.normalizeSearch(canceledSemi.getPlayer1());
        String c2 = StringUtils.normalizeSearch(canceledSemi.getPlayer2());

        String removed = (c1.equals(f1) || c1.equals(f2)) ? c2 : c1;
        result.getPlaceMap().put(removed, 4);

        semis.stream()
                .filter(m -> !isCanceled(m))
                .findFirst()
                .ifPresent(m -> {
                    String p1 = StringUtils.normalizeSearch(m.getPlayer1());
                    String p2 = StringUtils.normalizeSearch(m.getPlayer2());
                    String loser = m.getScore1() > m.getScore2() ? p2 : p1;
                    result.getPlaceMap().put(loser, 3);
                });

        return result;
    }

    private boolean isCanceled(Match m) {
        return m.getStatus() != null && m.getStatus().toLowerCase().contains("отмен");
    }
}