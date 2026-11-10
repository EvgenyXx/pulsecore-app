package ru.pulsecore.app.tournament.application.cascade;

import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;

import java.util.List;
import java.util.Optional;

public record ProcessedTournament(
        TournamentEntity tournament,
        List<TournamentMatchEntity> matches,
        Optional<TournamentResultEntity> result
) {}