package ru.pulsecore.app.modules.admin_modules.infrastructure.clinet;

import ru.pulsecore.app.modules.shared.dto.AdminCalculateResponse;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;

import java.util.UUID;

public interface TournamentClient {

    /**
     * Пересчитывает заработок игрока за указанный период.
     *
     * @param name      имя игрока
     * @param startDate начальная дата (yyyy-MM-dd)
     * @param endDate   конечная дата (yyyy-MM-dd), если null — только startDate
     * @return результат с суммой, количеством турниров и списком
     */
    AdminCalculateResponse calculate(String name, String startDate, String endDate);


    /**
     * Удаляет все турниры выбранного игрока.
     * @param playerId ID игрока
     * @return сообщение с количеством удалённых турниров
     */
    MessageResponse deleteAllTournaments(UUID playerId);

    /**
     * Перезагружает всю историю результатов игрока в фоне.
     * @param playerId ID игрока
     * @return сообщение о запуске перезагрузки
     */
    MessageResponse resyncAll(UUID playerId);
}
