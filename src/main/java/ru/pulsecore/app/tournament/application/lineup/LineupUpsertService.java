package ru.pulsecore.app.tournament.application.lineup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.mapping.TournamentToLineupMapper;
import ru.pulsecore.app.tournament.application.roster.TournamentDataProvider;
import ru.pulsecore.app.tournament.infrastructure.cache.LineupHashCache;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;



/**
 * Загружает, обновляет и сохраняет составы турниров на ближайшие дни.
 * Используется LineupScheduler для регулярной загрузки.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LineupUpsertService {

    private final LineupSaver lineupSaver;
    private final TournamentDataProvider tournamentDataProvider;
    private final TournamentToLineupMapper mapper;
    private final LineupStreamEnricher enrichStreamUrls;
    private final LineupHashCache lineupHashCache;
    private final ObjectMapper objectMapper;


    @Transactional
    public void loadDay(LocalDate date) {
        List<TournamentDto> all = tournamentDataProvider.getTournamentByDate(date);

        String hash = calculateHash(all);
        if (hash == null) {
            log.error("Пропуск обновления расписания на {}: не удалось посчитать хеш", date);
            return;
        }
        if (lineupHashCache.hasSameHash(date, hash)) {
            return;
        }

        List<Lineup> lineups = all.stream().map(t ->
                        mapper.toEntity(t, date, DateTimeUtils.formatTime(t.getDate().getDate())))
                .toList();
        enrichStreamUrls.enrich(lineups);
        lineupSaver.saveAll(lineups);
        lineupHashCache.update(date, hash);

        log.info("{} составов сохранено за дату {}", lineups.size(), date);
    }


    private String calculateHash(List<TournamentDto> all) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(all);
            return Integer.toHexString(Arrays.hashCode(bytes));
        } catch (JsonProcessingException e) {
            log.warn("Не удалось вычислить хеш турниров: {}", e.getMessage());
            return null;
        }
    }


}