package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.repository.HallStreamRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LineupStreamEnricher {

    private final HallStreamRepository hallStreamRepository;

    // TODO: Переделать на batch-запрос (один запрос в БД для всех залов через Map),
    //  чтобы избавиться от N+1 запросов в цикле.
    public void enrich(List<Lineup> lineups) {
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
}