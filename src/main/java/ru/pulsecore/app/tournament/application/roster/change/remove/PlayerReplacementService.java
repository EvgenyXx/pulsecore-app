package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerReplacementService {

    private final PlayerChangeNotificationPublisher notificationPublisher;
    private final PlayerNotificationRepository notificationRepository;
    private final PlayerTransferDetector transferDetector;
    private final PlayerRemovalDetector playerRemovalDetector;
    private final PlayerNotificationCreator playerNotificationCreator;

    @Transactional
    public boolean processReplacement(
            List<PlayerData> oldPlayers,
            TournamentDto newTournament,
            Long oldTournamentId,
            Map<String, List<TournamentDto>> allTournaments) {

        List<String> removedNames = playerRemovalDetector.findRemovedNames(oldPlayers, newTournament);
        if (removedNames.isEmpty()) return false;

        List<PlayerData> removedPlayers = playerRemovalDetector.findPlayerForReplace(removedNames, oldPlayers);

        removedPlayers.forEach(removedPlayer ->
                processRemovedPlayer(removedPlayer, newTournament, oldTournamentId, allTournaments));

        logReplacement(oldPlayers, newTournament, removedNames);
        return true;
    }

    private void processRemovedPlayer(
            PlayerData removedPlayer,
            TournamentDto newTournament,
            Long oldTournamentId,
            Map<String, List<TournamentDto>> allTournaments) {

        TransferInfo transferInfo = transferDetector.findTransfer(
                removedPlayer, newTournament, allTournaments);

        if (transferInfo != null) {
            processTransfer(removedPlayer, transferInfo);
        } else {
            processRemoval(removedPlayer, newTournament);
        }

        removeNotification(removedPlayer.playerId(), oldTournamentId);
    }

    private void processTransfer(PlayerData player, TransferInfo transferInfo) {
        notificationPublisher.sendTransferNotification(player, transferInfo);
        playerNotificationCreator.createNotificationForTransfer(player, transferInfo.to());
    }

    private void processRemoval(PlayerData player, TournamentDto tournament) {
        notificationPublisher.sendReplacementNotification(player, tournament);
    }

    private void removeNotification(UUID playerId, Long tournamentId) {
        notificationRepository.deleteByPlayerIdAndTournamentId(playerId, tournamentId);
    }

    private void logReplacement(@NonNull List<PlayerData> oldPlayers, TournamentDto newTournament, List<String> removedNames) {
        log.info("Player replacement: tournament={}, removedPlayers={}, compositionBefore={}, compositionAfter={}",
                newTournament.getId(),
                removedNames,
                oldPlayers.stream().map(PlayerData::playerName).toList(),
                newTournament.getPlayers());
    }
}