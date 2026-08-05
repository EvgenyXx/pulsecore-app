package ru.pulsecore.app.tournament.application.tournament.calculation.removed.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import ru.pulsecore.app.tournament.domain.MatchStage;
import ru.pulsecore.app.tournament.application.tournament.calculation.DefaultMatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.RemovedPlayerHandler;
import ru.pulsecore.app.tournament.domain.RemovedStage;
import ru.pulsecore.app.tournament.domain.MatchProcessingResult;
import ru.pulsecore.app.tournament.domain.TournamentContext;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RemovedInGroupHandler implements RemovedPlayerHandler {

    private final DefaultMatchCalculationStrategy defaultStrategy;

    @Override
    public RemovedStage getStage() {
        return RemovedStage.GROUP;
    }

    @Override
    public MatchProcessingResult handle(TournamentContext ctx) {
        String removed = StringUtils.normalizeSearch(ctx.getRemovedPlayer());

        MatchProcessingResult result = defaultStrategy.process(ctx);

        Set<String> finalists = extractFinalists(ctx);
        result.getPlaceMap().put(removed, 4);

        findThirdPlaceCandidate(ctx, removed, finalists)
                .ifPresent(p -> result.getPlaceMap().put(p, 3));

        return result;
    }

    private Set<String> extractFinalists(TournamentContext ctx) {
        Set<String> finalists = new HashSet<>();

        ctx.getMatches().stream()
                .filter(m -> MatchStage.FINAL.matches(m.getStage()))
                .findFirst()
                .ifPresent(finalMatch -> {
                    finalists.add(StringUtils.normalizeSearch(finalMatch.getPlayer1()));
                    finalists.add(StringUtils.normalizeSearch(finalMatch.getPlayer2()));
                });

        return finalists;
    }

    private Optional<String> findThirdPlaceCandidate(TournamentContext ctx, String removed, Set<String> finalists) {
        return ctx.getMatches().stream()
                .flatMap(m -> Stream.of(m.getPlayer1(), m.getPlayer2()))
                .map(StringUtils::normalizeSearch)
                .distinct()
                .filter(p -> !p.equals(removed))
                .filter(p -> !finalists.contains(p))
                .findFirst();
    }
}