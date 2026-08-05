package ru.pulsecore.app.modules.tournament.domain;

public record RemovedResult(
        String player,
        RemovedStage stage
) {}