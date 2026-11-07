package ru.pulsecore.app.tournament.application.match;

import ru.pulsecore.app.tournament.domain.enums.MatchStage;

public interface StageParser {
    MatchStage parse(String stage);
}