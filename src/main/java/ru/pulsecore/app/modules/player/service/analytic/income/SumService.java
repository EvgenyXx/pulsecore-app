// ==================== SumService.java — заменить ====================
package ru.pulsecore.app.modules.player.service.analytic.income;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.player.api.dto.SumResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SumService {

    private final PlayerService playerService;
    private final TournamentResultService tournamentResultService;

    public SumResponse getSum(UUID id, LocalDate start, LocalDate end, int page, int size) {
        Player player = playerService.getById(id);
        if (start == null && end == null)
            return SumResponse.builder().playerName(capitalize(player.getName())).start("").end("")
                    .sum(0.0).average(0.0).count(0L).tournaments(null).totalPages(0).currentPage(0).totalElements(0).build();
        if (start == null) start = end;
        if (end == null) end = start;

        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, start, end);
        Page<TournamentResultEntity> pageResult = tournamentResultService.getResultsByPeriod(player, start, end, PageRequest.of(page, size));

        var tournaments = pageResult.getContent().stream()
                .map(e -> SumResponse.TournamentItem.builder()
                        .date(e.getDate().toString()).amount(e.getAmount())
                        .resultId(e.getId()).hasRemoved(e.isHasRemoved()).build())
                .collect(Collectors.toList());

        return SumResponse.builder()
                .playerName(capitalize(player.getName())).start(start.toString()).end(end.toString())
                .sum(stats != null ? stats.getSum() : 0)
                .average(stats != null ? stats.getAverage() : 0)
                .count(stats != null ? stats.getCount() : 0)
                .tournaments(tournaments)
                .totalPages(pageResult.getTotalPages())
                .currentPage(pageResult.getNumber())
                .totalElements(pageResult.getTotalElements())
                .build();
    }

    private String capitalize(String name) {
        if (name == null || name.isBlank()) return name;
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}