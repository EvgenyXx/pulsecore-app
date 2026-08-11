package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.client.MastersApiClient;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.persistence.mapper.LineupMapper;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.HallStreamRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.LineupRepository;
import ru.pulsecore.app.tournament.infrastructure.util.TournamentDateUtils;
import ru.pulsecore.app.tournament.infrastructure.validator.TournamentValidator;
import java.time.LocalDate;
import java.util.List;

/**
 * Загружает, обновляет и сохраняет составы турниров на ближайшие дни.
 * Используется LineupScheduler для регулярной загрузки.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LineupUpsertService {

    private final LineupRepository lineupRepository;
    private final MastersApiClient apiClient;
    private final LineupMapper mapper;
    private final TournamentValidator validator;
    private final HallStreamRepository hallStreamRepository;

//todo добавиит кеширование если количество составов не обновилось не перепроверять
    @Transactional
    public void loadDay(LocalDate date) {
        List<TournamentDto> all = apiClient.loadTournaments(date.toString());
        if (all == null || all.isEmpty()) return;

        List<TournamentDto> valid = filterValid(all, date);
        if (valid.isEmpty()) return;

        clearFutureLineups(date);
        List<Lineup> lineups = mapToLineups(valid, date);
        enrichStreamUrls(lineups);
        saveLineups(lineups);

        log.info("{} составов сохранено за дату {}", lineups.size(), date);
    }

    private List<TournamentDto> filterValid(List<TournamentDto> all, LocalDate date) {
        return all.stream()
                .filter(validator::isValid)
                .filter(t -> date.equals(TournamentDateUtils.extractDate(t)))
                .toList();
    }

    private void clearFutureLineups(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            lineupRepository.deleteAllByDate(date);
        }
    }

    private List<Lineup> mapToLineups(List<TournamentDto> valid, LocalDate date) {
        return valid.stream()
                .map(t -> mapper.toEntity(t, date, TournamentDateUtils.extractTime(t)))
                .toList();
    }

    private void enrichStreamUrls(List<Lineup> lineups) {
        lineups.forEach(lineup -> {
            if (lineup.getStreamUrl() == null || lineup.getStreamUrl().isBlank()) {
                try {
                    String url = hallStreamRepository.findStreamUrlByHall(lineup.getHall());
                    if (url != null) lineup.setStreamUrl(url);
                } catch (Exception e) {
                    log.warn("Нет URL стрима для зала {}", lineup.getHall());
                }
            }
        });
    }

    private void saveLineups(List<Lineup> lineups) {
        lineups.forEach(lineup -> lineupRepository.upsertLineup(
                lineup.getDate(), lineup.getLeague(), lineup.getTime(),
                lineup.getHall(), lineup.getPlayers(), lineup.getStreamUrl()));
    }


}