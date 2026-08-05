package ru.pulsecore.app.tournament.domain;

public record RemovedResult(
        String player,
        RemovedStage stage
) {}