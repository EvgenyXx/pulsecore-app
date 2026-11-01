package ru.pulsecore.app.tournament.application.roster.change;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.remove.PlayerChangeNotificationPublisher;
import ru.pulsecore.app.tournament.application.roster.change.remove.PlayerNotificationCreator;
import ru.pulsecore.app.tournament.application.roster.change.remove.PlayerTransferDetector;
import ru.pulsecore.app.tournament.application.roster.change.remove.TransferDetector;
import ru.pulsecore.app.tournament.infrastructure.repository.PlayerNotificationRepository;
import java.util.List;
import java.util.Map;

/**
 * Обработчик одного удаленного игрока.
 *
 * <p>Определяет судьбу игрока через {@link PlayerTransferDetector}:</p>
 * <ul>
 *     <li>Если игрок найден в другом турнире (TRANSFERRED) — отправляет уведомление о переносе
 *         и создает новую связь с новым турниром.</li>
 *     <li>Если игрок нигде не найден (REMOVED) — отправляет уведомление о снятии с турнира.</li>
 * </ul>
 *
 * <p>В конце в любом случае удаляет старую связь игрока с исходным турниром.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RemovedPlayerProcessor {

    private final PlayerChangeNotificationPublisher notificationPublisher;
    private final PlayerNotificationRepository notificationRepository;
    private final TransferDetector transferDetector;
    private final PlayerNotificationCreator playerNotificationCreator;

    public void process(PlayerData removedPlayer, TournamentDto newTournament,
                        Long oldTournamentId,
                        Map<String, List<TournamentDto>> allTournaments) {

        TransferResult transferInfo = transferDetector.findTransfer(
                removedPlayer, newTournament, allTournaments);

        switch (transferInfo.status()) {
            case TRANSFERRED -> {
                log.debug("Замена: перенос игрока={}", removedPlayer.name());
                notificationPublisher.sendTransferNotification(removedPlayer, transferInfo.info());
                playerNotificationCreator.createNotificationForTransfer(removedPlayer, transferInfo.info().to());
            }
            case REMOVED -> {
                log.debug("Замена: снятие игрока={}", removedPlayer.name());
                notificationPublisher.sendReplacementNotification(removedPlayer, newTournament);
            }
        }
        notificationRepository.deleteByPlayerIdAndTournamentId(removedPlayer.id(), oldTournamentId);
    }
}
