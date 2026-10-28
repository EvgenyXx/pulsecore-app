package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.client.MastersApiClient;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Сервис поиска и фильтрации турниров во внешнем API Лиги Мастерс.
 *
 * <p>Класс является связующим звеном между внешним API ({@link MastersApiClient})
 * и внутренней бизнес-логикой приложения. Он не сохраняет данные в БД, а только
 * загружает их и отдает тем, кому они нужны.</p>
 *
 * <p><b>Кто с ним работает:</b></p>
 * <ul>
 *     <li>{@code TournamentAutoAddService} — при первичной регистрации игрока или
 *     синхронизации его истории (CascadeSync) для получения списка URL турниров и
 *     их дальнейшего парсинга.</li>
 *     <li>{@code AdminCalculateService} — при ручном пересчете зарплаты игрока
 *     администратором в админ-панели для получения списка турниров за период.</li>
 * </ul>
 *
 * <p><b>Как работает:</b></p>
 * <ol>
 *     <li>Принимает диапазон дат и имя игрока (в сыром виде).</li>
 *     <li>Идет в API Лиги Мастерс по каждому дню диапазона.</li>
 *     <li>Фильтрует полученный список, оставляя только те турниры, где
 *     в списке участников есть искомый игрок.</li>
 *     <li>Возвращает готовый список DTO турниров ({@link TournamentDto}).</li>
 * </ol>
 *
 * <p><b>Особенности фильтрации:</b> Сравнение имен происходит строго
 * (через {@link PlayerIdentity#isSamePlayer(String, String)}), чтобы избежать путаницы между
 * тезками. Например, "Иванов Михаил" не будет совпадать с "Иванов Михаил Юр".</p>
 *
 * @see TournamentAutoAddService
 * @see ru.pulsecore.app.tournament.application.admin.AdminCalculateService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSearchService {

    private static final long API_DELAY_MS = 500;

    private final MastersApiClient mastersApiClient;



    public List<TournamentDto> findByDateRangeAndPlayer(String startDate, String endDate, String playerName) {
        String searchName = NameNormalizer.normalizeForSearch(playerName);
        List<TournamentDto> allTournaments = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        for (LocalDate current = start; !current.isAfter(end); current = current.plusDays(1)) {
            List<TournamentDto> dayTournaments = loadDayTournaments(current.toString());
            allTournaments.addAll(filterByPlayer(dayTournaments, searchName));
            sleepBetweenRequests();
        }

        return allTournaments;
    }

    private List<TournamentDto> loadDayTournaments(String date) {
        try {
            return mastersApiClient.loadTournaments(date);
        } catch (Exception e) {
            log.error("Failed to load tournaments for {}: {}", date, e.getMessage());
            return List.of();
        }
    }

    private List<TournamentDto> filterByPlayer(List<TournamentDto> tournaments, String searchName) {
        return tournaments.stream()
                .filter(t -> t.getPlayers() != null && t.getPlayers().stream()
                        .anyMatch(p -> PlayerIdentity.isSamePlayer(p,searchName)))
                .toList();
    }

    private void sleepBetweenRequests() {
        try {
            TimeUnit.MILLISECONDS.sleep(API_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}