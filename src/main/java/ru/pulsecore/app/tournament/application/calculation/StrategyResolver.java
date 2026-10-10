package ru.pulsecore.app.tournament.application.calculation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.MatchCalculationStrategy;
import ru.pulsecore.app.tournament.domain.enums.StrategyType;
import ru.pulsecore.app.tournament.domain.model.TournamentContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StrategyResolver {

    private final Map<StrategyType, MatchCalculationStrategy> strategyMap;

    public StrategyResolver(List<MatchCalculationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        MatchCalculationStrategy::getType,
                        s -> s
                ));
    }

    public MatchCalculationStrategy resolve(TournamentContext ctx) {
        StrategyType type = resolveType(ctx);

        MatchCalculationStrategy strategy = strategyMap.get(type);

        if (strategy == null) {
            throw new IllegalStateException("No strategy found for type: " + type);
        }


        return strategy;
    }

   private StrategyType resolveType(TournamentContext ctx) {
    return switch (ctx.getVersion()) {
        case FOUR_PL_NEW -> switch (ctx.getWithdrawalStage()) {
            case NONE -> StrategyType.FOUR_PL_BRACKET_DEFAULT;
            case BEFORE_PLAYOFF -> StrategyType.FOUR_PL_BRACKET_REMOVED_EARLY;
            case PLAYOFF -> StrategyType.FOUR_PL_BRACKET_REMOVED_PLAYOFF;
        };

        case STANDARD -> {
            boolean removed = ctx.getRemovedPlayer() != null && !ctx.getRemovedPlayer().isBlank();
            if (!removed) {
                yield StrategyType.STANDARD_DEFAULT;
            }
            yield StrategyType.STANDARD_REMOVED;
        }
    };
}
}