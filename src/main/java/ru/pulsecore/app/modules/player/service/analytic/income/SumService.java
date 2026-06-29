package ru.pulsecore.app.modules.player.service.analytic.income;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.player.api.dto.sum.SumResponse;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.shared.util.StringUtils;
import ru.pulsecore.app.modules.tournament.application.TournamentResultService;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SumService {

    private final PlayerService playerService;
    private final TournamentResultService tournamentResultService;

    public SumResponse getSum(UUID id, LocalDate start, LocalDate end, int page, int size) {
        Player player = playerService.getById(id);
        if (start == null && end == null) {
            return emptyResponse(player);
        }
        if (start == null) start = end;
        if (end == null) end = start;

        PeriodStatsProjection stats = tournamentResultService.getStatsByPeriod(player, start, end);
        Page<TournamentResultEntity> pageResult = tournamentResultService.getResultsByPeriod(
                player, start, end, PageRequest.of(page, size));

        return SumResponse.builder()
                .playerName(StringUtils.capitalize(player.getName()))
                .start(start.toString())
                .end(end.toString())
                .sum(stats != null ? stats.getSum() : 0)
                .average(stats != null ? stats.getAverage() : 0)
                .count(stats != null ? stats.getCount() : 0)
                .tournaments(buildTournamentItems(pageResult))
                .totalPages(pageResult.getTotalPages())
                .currentPage(pageResult.getNumber())
                .totalElements(pageResult.getTotalElements())
                .build();
    }

    private SumResponse emptyResponse(Player player) {
        return SumResponse.builder()
                .playerName(StringUtils.capitalize(player.getName()))
                .start("").end("")
                .sum(0.0).average(0.0).count(0L)
                .tournaments(null).totalPages(0).currentPage(0).totalElements(0)
                .build();
    }

    private List<SumResponse.TournamentItem> buildTournamentItems(Page<TournamentResultEntity> pageResult) {
        return pageResult.getContent().stream()
                .map(e -> SumResponse.TournamentItem.builder()
                        .date(e.getDate().toString())
                        .amount(e.getAmount())
                        .resultId(e.getId())
                        .hasRemoved(e.isHasRemoved())
                        .build())
                .toList();
    }
}