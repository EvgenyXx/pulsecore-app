package ru.pulsecore.app.admin.client;

import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.shared.dto.response.AdminCalculateResponse;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.shared.dto.response.MessageResponse;

import java.time.LocalDate;
import java.util.List;
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

    List<AdminTournamentResponse> getTournamentsByDate(LocalDate date);

    AdminTournamentResponse getTournamentById(Long id);

    AdminTournamentResponse updateTournament(Long id, UpdateTournamentRequest request);
}
