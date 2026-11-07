package ru.pulsecore.app.tournament.application.match;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.enums.MatchStage;
import ru.pulsecore.app.tournament.domain.enums.LegacyMatchStage;

@Component
@Profile("stage-legacy")
public class LegacyStageParser implements StageParser {

    @Override
    public MatchStage parse(String stage) {
        for (LegacyMatchStage legacy : LegacyMatchStage.values()) {
            if (legacy.matches(stage)) {
                return switch (legacy) {
                    case FINAL -> MatchStage.FINAL;
                    case SEMI_FINAL -> MatchStage.SEMIFINAL;
                    case THIRD_PLACE -> MatchStage.THIRD_PLACE;
                    case GROUP -> MatchStage.GROUP;
                };
            }
        }

        return MatchStage.GROUP;
    }
}