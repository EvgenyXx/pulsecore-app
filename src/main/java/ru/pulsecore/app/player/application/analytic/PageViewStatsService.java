package ru.pulsecore.app.player.application.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PageViewStats;
import ru.pulsecore.app.shared.dto.response.PlayerPageViewStats;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PlayerStatsRepository;
import ru.pulsecore.app.player.infrastructure.persistence.repository.PageViewRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageViewStatsService {

    private final PageViewRepository pageViewRepository;
    private final EndpointLabelRegistry labelRegistry;
    private final PlayerStatsRepository playerStatsRepository;

    public List<PlayerPageViewStats> getPlayerStats(int days) {
        Instant from = Instant.now().minus(days, ChronoUnit.DAYS);
        return playerStatsRepository.getPlayerStats(from).stream()
                .map(row -> {
                    String[] pathPairs = row.getPaths().split("\\|");
                    long total = row.getTotal();

                    List<PlayerPageViewStats.PathCount> paths = Arrays.stream(pathPairs)
                            .map(pair -> {
                                int eq = pair.lastIndexOf('=');
                                String rawPath = pair.substring(0, eq);
                                long count = Long.parseLong(pair.substring(eq + 1));
                                return new PlayerPageViewStats.PathCount(labelRegistry.resolve(rawPath), count);
                            })
                            .collect(Collectors.toList());

                    return new PlayerPageViewStats(row.getName(), paths, total, row.getPercent());
                })
                .toList();
    }

    public List<PageViewStats> getStats(int days) {
        Instant from = Instant.now().minus(days, ChronoUnit.DAYS);

        return pageViewRepository.findStatsSince(from).stream()
                .map(p -> new PageViewStats(
                        labelRegistry.resolve(p.getPath()),
                        p.getMethod(),
                        p.getCount()
                ))
                .toList();
    }


}