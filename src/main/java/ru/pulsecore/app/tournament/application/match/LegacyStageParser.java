package ru.pulsecore.app.tournament.application.match;


import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.enums.MatchStage;
import ru.pulsecore.app.tournament.domain.enums.LegacyMatchStage;
import ru.pulsecore.app.tournament.domain.enums.TournamentVersion;

@Component
public class LegacyStageParser implements StageParser {

    @Override
    public boolean supports(TournamentVersion version) {
        return version == TournamentVersion.STANDARD;
    }

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