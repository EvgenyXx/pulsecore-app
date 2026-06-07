// modules/admin/service/PageViewStatsService.java
package ru.pulsecore.app.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.admin.dto.PageViewStats;
import ru.pulsecore.app.modules.shared.repository.PageViewRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageViewStatsService {

    private final PageViewRepository pageViewRepository;
    private final EndpointLabelRegistry labelRegistry;

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