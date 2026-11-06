package ru.pulsecore.app.tournament.application.roster;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.client.MastersApi;
import ru.pulsecore.app.tournament.infrastructure.config.RateLimiterConfig;

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
public class TournamentDataProvider {

    private static final int FORECAST_DAYS = 3;
    private final MastersApi mastersApi;
    private final Bucket apiSyncRateLimiter;

    public TournamentDataProvider(
            MastersApi mastersApi,
            @Qualifier(RateLimiterConfig.API_SYNC_RATE_LIMITER) Bucket apiSyncRateLimiter) {
        this.mastersApi = mastersApi;
        this.apiSyncRateLimiter = apiSyncRateLimiter;
    }

    /**
     * Быстрый запрос для отображения расписания (без лимитов и пауз).
     */
    public List<TournamentDto> getTournamentForView(LocalDate day) {
        return mastersApi.loadTournaments(day.toString());
    }

    /**
     * Запрос для синхронизации истории (с лимитом и паузами).
     */
    public List<TournamentDto> getTournamentForSync(LocalDate day) {
        try {
            apiSyncRateLimiter.asBlocking().consume(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        }
        return mastersApi.loadTournaments(day.toString());
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
            try {
                apiSyncRateLimiter.asBlocking().consume(1);
                all.put(date, mastersApi.loadTournaments(date));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Синхронизация прервана для даты {}", date);
                break;
            }
        }
        return all;
    }


}
