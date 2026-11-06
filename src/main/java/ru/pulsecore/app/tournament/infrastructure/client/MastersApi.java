package ru.pulsecore.app.tournament.infrastructure.client;

import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.util.List;

public interface MastersApi {
    List<TournamentDto> loadTournaments(String date);
}