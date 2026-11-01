package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.application.roster.change.TransferResult;
import ru.pulsecore.app.tournament.infrastructure.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Primary
@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizedTransferDetector implements TransferDetector {

    private final PlayerNotificationRepository notificationRepository;

    @Override
    public TransferResult findTransfer(
            PlayerData removedPlayer,
            TournamentDto oldTournament,
            Map<String, List<TournamentDto>> allTournaments) {

        log.info("🚀 [OPTIMIZED] Поиск переноса для игрока={}", removedPlayer.name());

        log.debug("Оптимизированный поиск для игрока={}, из турнира={}",
                removedPlayer.name(), oldTournament.getLink());

        TournamentDto newTournament = findNewTournament(removedPlayer, oldTournament, allTournaments);

        if (newTournament == null) {
            return TransferResult.removed();
        }

        return TransferResult.transferred(TransferInfo.of(oldTournament, newTournament));
    }

    private TournamentDto findNewTournament(
            PlayerData removedPlayer,
            TournamentDto oldTournament,
            Map<String, List<TournamentDto>> allTournaments) {


        Set<String> alreadyLinkedLinks = notificationRepository
                .findTournamentLinksByPlayerId(removedPlayer.id());

        for (List<TournamentDto> dayTournaments : allTournaments.values()) {
            for (TournamentDto tournament : dayTournaments) {
                if (tournament.getId().equals(oldTournament.getId())) continue;
                if (alreadyLinkedLinks.contains(tournament.getLink())) continue;
                if (tournament.getPlayers() == null) continue;

                boolean inRoster = tournament.getPlayers().stream()
                        .anyMatch(name -> PlayerIdentity.isSamePlayer(name, removedPlayer.name()));

                if (inRoster) {
                    return tournament;
                }
            }
        }

        return null;
    }
}