package ru.pulsecore.app.tournament.application.roster.change.remove;

import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.TransferResult;

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
public interface TransferDetector {

     /**
     * Ищет перенос игрока в другой турнир.
     *
     * @param removedPlayer  игрок, которого сняли
     * @param oldTournament  турнир, из которого сняли
     * @param allTournaments все турниры за 3 дня (для поиска)
     * @return TransferInfo если перенос найден, иначе null
     */
    TransferResult findTransfer(
            PlayerData removedPlayer,
            TournamentDto oldTournament,
            Map<String, List<TournamentDto>> allTournaments);
}