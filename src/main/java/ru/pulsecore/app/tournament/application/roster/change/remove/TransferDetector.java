package ru.pulsecore.app.tournament.application.roster.change.remove;

import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.TransferResult;

import java.util.List;
import java.util.Map;

public interface TransferDetector {
    TransferResult findTransfer(
            PlayerData removedPlayer,
            TournamentDto oldTournament,
            Map<String, List<TournamentDto>> allTournaments);
}