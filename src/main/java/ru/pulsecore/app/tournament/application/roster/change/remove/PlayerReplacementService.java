package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.RemovedPlayerProcessor;
import ru.pulsecore.app.tournament.application.roster.change.TournamentRosterAuditService;
import java.util.*;

/**
 * Сервис-оркестратор процесса замены игроков в турнире.
 *
 * <p>Логика:</p>
 * <ol>
 *     <li>Находит игроков, которые пропали из нового состава (по сравнению со старым)</li>
 *     <li>Для каждого пропавшего игрока запускает обработку (перенос в другой турнир или снятие)
 *         через {@link RemovedPlayerProcessor}</li>
 *     <li>Записывает результат замены в аудит через {@link TournamentRosterAuditService}</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerReplacementService {


    private final PlayerRemovalDetector playerRemovalDetector;
    private final TournamentRosterAuditService rosterAuditService;
    private final RemovedPlayerProcessor processRemovedPlayer;

    @Transactional
    public boolean processReplacement(
            List<PlayerData> oldPlayers,
            TournamentDto newTournament,
            Long oldTournamentId,
            Map<String, List<TournamentDto>> allTournaments) {

        List<String> removedNames = playerRemovalDetector.findRemovedNames(oldPlayers, newTournament);
        if (removedNames.isEmpty()) return false;

        log.debug("Замена: найдены удалённые игроки={}, турнир={}",
                removedNames, newTournament.getLink());

        List<PlayerData> removedPlayers = playerRemovalDetector.findPlayerForReplace(removedNames, oldPlayers);

        removedPlayers.forEach(removedPlayer -> {
            log.debug("Замена: обработка игрока={}", removedPlayer.name());
            processRemovedPlayer.process(removedPlayer, newTournament, oldTournamentId, allTournaments);
        });

        rosterAuditService.logReplacement(oldPlayers, newTournament, removedNames);
        return true;
    }


}