package ru.pulsecore.app.tournament.application.sum;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentResultNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PeriodStatsProjection;
import ru.pulsecore.app.player.api.dto.response.SumResponse;

import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.application.result.TournamentResultService;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SumService {

    private final PlayerClient playerClient;
    private final TournamentResultService tournamentResultService;
    private final TournamentResultRepository tournamentResultRepository;


    public void updateResult(Long id, Double amount, Double bonus) {
        TournamentResultEntity result = tournamentResultRepository.findById(id)
                .orElseThrow(() -> new TournamentResultNotFoundException(id));
        if (amount != null) result.setAmount(amount);
        if (bonus != null) result.setBonus(bonus);
        tournamentResultRepository.save(result);

    }

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