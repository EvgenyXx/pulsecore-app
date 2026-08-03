package ru.pulsecore.app.modules.tournament_module.service.extraction;

import ru.pulsecore.app.modules.tournament_module.service.calculation.strategy.removed.RemovedStage;

public record RemovedResult(
        String player,
        RemovedStage stage
) {}