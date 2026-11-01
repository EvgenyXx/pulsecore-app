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

import java.util.List;
import java.util.Map;

/**
 * Определяет, был ли игрок перенесён в другой турнир или полностью снят.
 *
 * <p>Логика:</p>
 * <ol>
 *     <li>Игрок удалён из старого турнира</li>
 *     <li>Ищем его в других турнирах за 3 дня</li>
 *     <li>Если нашли и он не привязан — это перенос</li>
 *     <li>Если не нашли — это снятие</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerTransferDetector implements TransferDetector{

    private final PlayerNotificationRepository notificationRepository;

    /**
     * Ищет перенос игрока в другой турнир.
     *
     * @param removedPlayer  игрок, которого сняли
     * @param oldTournament  турнир, из которого сняли
     * @param allTournaments все турниры за 3 дня (для поиска)
     * @return TransferInfo если перенос найден, иначе null
     */
    @Override
    public TransferResult findTransfer(
            PlayerData removedPlayer,
            TournamentDto oldTournament,
            Map<String, List<TournamentDto>> allTournaments) {

        log.debug("Перенос: поиск для игрока={}, из турнира={}",
                removedPlayer.name(), oldTournament.getLink());
        log.info("🔍 [STANDARD] Поиск переноса для игрока={}", removedPlayer.name());

        TournamentDto newTournament = findNewTournament(removedPlayer, oldTournament, allTournaments);

        if (newTournament == null) {
            log.debug("Игрок {} снят, перенос не найден", removedPlayer.name());
            return TransferResult.removed();
        }

        log.info("🔄 Игрок {} перенесён: {} ({}) -> {} ({})",
                removedPlayer.name(),
                oldTournament.getTitle(), oldTournament.getId(),
                newTournament.getTitle(), newTournament.getId());

        return TransferResult.transferred(TransferInfo.of(oldTournament, newTournament));
    }

    /**
     * Ищет игрока в других турнирах.
     */
    private TournamentDto findNewTournament(
            PlayerData removedPlayer,
            TournamentDto oldTournament,
            Map<String, List<TournamentDto>> allTournaments) {

        String normalizedName = NameNormalizer.normalizeForSearch(removedPlayer.name());

        for (List<TournamentDto> dayTournaments : allTournaments.values()) {
            for (TournamentDto tournament : dayTournaments) {
                // Пропускаем тот же турнир
                if (tournament.getId().equals(oldTournament.getId())) continue;

                // Проверяем, есть ли игрок в составе этого турнира
                boolean inRoster = tournament.getPlayers() != null && tournament.getPlayers().stream()
                        .map(NameNormalizer::normalizeForSearch)
                        .anyMatch(name -> name.equals(normalizedName));

                if (!inRoster) continue;

                // Проверяем, не привязан ли уже игрок к этому турниру
                boolean alreadyLinked = notificationRepository
                        .findByTournamentLink(tournament.getLink())
                        .stream()
                        .anyMatch(pn -> pn.getPlayerId().equals(removedPlayer.id()));

                if (!alreadyLinked) {
                    log.debug("Перенос: найден новый турнир={}", tournament.getLink());
                    return tournament;
                }
            }
        }

        return null;
    }
}