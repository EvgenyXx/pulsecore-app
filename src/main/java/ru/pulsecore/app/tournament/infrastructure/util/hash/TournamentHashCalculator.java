package ru.pulsecore.app.tournament.infrastructure.util.hash;

import ru.pulsecore.app.shared.dto.response.TournamentDto;

public interface TournamentHashCalculator {



    String calculateTournamentHash(TournamentDto tournament);
}