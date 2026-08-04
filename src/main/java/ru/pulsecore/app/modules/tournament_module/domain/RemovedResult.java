package ru.pulsecore.app.modules.tournament_module.domain;

public record RemovedResult(
        String player,
        RemovedStage stage
) {}