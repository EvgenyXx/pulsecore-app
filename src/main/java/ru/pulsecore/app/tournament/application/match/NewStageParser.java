package ru.pulsecore.app.tournament.application.match;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.enums.GameStage;
import ru.pulsecore.app.tournament.domain.enums.MatchStage;
import ru.pulsecore.app.tournament.domain.enums.TournamentVersion;

@Slf4j
@Component
public class NewStageParser implements StageParser {

    @Override
    public boolean supports(TournamentVersion version) {
        return version == TournamentVersion.FOUR_PL_NEW;
    }

    @Override
    public MatchStage parse(String stage) {
        if (stage == null) return MatchStage.GROUP;
        String s = stage.toLowerCase().trim();
        GameStage gameStage = GameStage.fromCode(s);
        if (gameStage == null) return MatchStage.GROUP;

        return switch (gameStage) {
            case FINAL -> MatchStage.FINAL;
            case TOP_MESH_1 -> MatchStage.TOP_MESH_1;
            case TOP_MESH_2 -> MatchStage.TOP_MESH_2;
            case TOP_MESH_3 -> MatchStage.TOP_MESH_3;
            case LOWER_MESH_1 -> MatchStage.LOWER_MESH_1;
            case LOWER_MESH_2 -> MatchStage.LOWER_MESH_2;
            case LOWER_MESH_3 -> MatchStage.LOWER_MESH_3;
        };
    }
}