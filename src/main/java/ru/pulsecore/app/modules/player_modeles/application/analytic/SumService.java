package ru.pulsecore.app.modules.player_modeles.application.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.PeriodStatsProjection;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.SumResponse;

import ru.pulsecore.app.modules.shared.util.StringUtils;
import ru.pulsecore.app.modules.tournament_module.client.PlayerClient;
import ru.pulsecore.app.modules.tournament_module.service.tornament.TournamentResultService;
import ru.pulsecore.app.modules.tournament_module.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SumService {

   private final PlayerClient playerClient;
    private final TournamentResultService tournamentResultService;

    public SumResponse getSum(UUID playerId, LocalDate start, LocalDate end, int page, int size) {
        var player = playerClient.getPlayerById(playerId);
        if (start == null && end == null) {
            return emptyResponse(player.playerName());
        }
        if (start == null) start = end;
        if (end == null) end = start;
        PeriodStatsProjection stats =
                tournamentResultService.getStatsByPeriod(player.playerId(), start, end);

        Page<TournamentResultEntity> pageResult = tournamentResultService.getResultsByPeriod(
                player.playerId(), start, end, PageRequest.of(page, size));

        return SumResponse.builder()
                .playerName(StringUtils.capitalize(player.playerName()))
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

    private SumResponse emptyResponse(String playerName) {
        return SumResponse.builder()
                .playerName(StringUtils.capitalize(playerName))
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