package ru.pulsecore.app.tournament.application.roster;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.client.MastersApiClient;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
 * используется в дискавери для поиска новых турниров
 * и чендж сервисе для поиска замены игроков в турнире или их удаления
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentDataProvider {

    private static final int FORECAST_DAYS = 3;
    private final MastersApiClient apiClient;

    public List<TournamentDto>getTournamentByDate(LocalDate day){
        return apiClient.loadTournaments(day.toString());
    }

    public List<TournamentDto> getTournamentsFor3days() {
        return getAllTournamentsFor3Days().values()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    public Map<String, List<TournamentDto>> getAllTournamentsFor3Days() {
        Map<String, List<TournamentDto>> all = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < FORECAST_DAYS; i++) {
            String date = today.plusDays(i).toString();

            all.put(date, apiClient.loadTournaments(date));
        }
        return all;
    }


}
