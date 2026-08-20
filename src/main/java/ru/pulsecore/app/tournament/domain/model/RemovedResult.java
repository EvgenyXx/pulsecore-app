package ru.pulsecore.app.tournament.domain.model;

import ru.pulsecore.app.tournament.domain.enums.RemovedStage;

public record RemovedResult(
        String player,
        RemovedStage stage
) {}