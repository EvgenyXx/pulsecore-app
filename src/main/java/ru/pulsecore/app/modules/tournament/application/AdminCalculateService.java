package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.ResultDto;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;
import ru.pulsecore.app.modules.tournament.api.dto.response.AdminCalculateResponse;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.service.TournamentSearchService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCalculateService {

    private final TournamentSearchService tournamentSearchService;
    private final ResultService resultService;
    private final NameNormalizer nameNormalizer;

    public AdminCalculateResponse calculate(String name, String startDate, String endDate) {
        if (endDate == null || endDate.isBlank()) {
            endDate = startDate;
        }

        List<TournamentDto> tournaments = tournamentSearchService.findByDateRangeAndPlayer(startDate, endDate, name);

        if (tournaments.isEmpty()) {
            return buildEmptyResponse(name, startDate, endDate);
        }

        return buildResponse(name, startDate, endDate, tournaments);
    }

    private AdminCalculateResponse buildEmptyResponse(String name, String startDate, String endDate) {
        return AdminCalculateResponse.builder()
                .playerName(name)
                .startDate(startDate)
                .endDate(endDate)
                .totalAmount(0)
                .tournamentCount(0)
                .tournaments(List.of())
                .build();
    }

    private AdminCalculateResponse buildResponse(String name, String startDate, String endDate, List<TournamentDto> tournaments) {
        String searchName = nameNormalizer.normalizeForSearch(name);
        List<AdminCalculateResponse.TournamentResultItem> items = new ArrayList<>();
        double totalAmount = 0;

        for (TournamentDto t : tournaments) {
            try {
                ParsedResult parsed = resultService.calculateAll(t.getLink());
                double playerAmount = extractPlayerAmount(parsed, searchName);

                if (playerAmount > 0) {
                    totalAmount += playerAmount;
                    items.add(buildTournamentItem(t, playerAmount, parsed));
                }
            } catch (Exception e) {
                log.warn("Failed to calculate tournament {}: {}", t.getLink(), e.getMessage());
            }
        }

        items.sort(Comparator.comparing(AdminCalculateResponse.TournamentResultItem::getDate));

        return AdminCalculateResponse.builder()
                .playerName(name)
                .startDate(startDate)
                .endDate(endDate)
                .totalAmount(totalAmount)
                .tournamentCount(items.size())
                .tournaments(items)
                .build();
    }

    private double extractPlayerAmount(ParsedResult parsed, String searchName) {
        return parsed.results().stream()
                .filter(r -> nameNormalizer.normalizeForSearch(r.getPlayer()).contains(searchName))
                .mapToDouble(ResultDto::getTotal)
                .sum();
    }

    private AdminCalculateResponse.TournamentResultItem buildTournamentItem(TournamentDto t, double playerAmount, ParsedResult parsed) {
        return AdminCalculateResponse.TournamentResultItem.builder()
                .date(t.getDate() != null ? t.getDate().getDate() : "—")
                .amount(playerAmount)
                .tournamentTitle(t.getTitle())
                .tournamentId(t.getId())
                .hasRemoved(parsed.hasRemoved())
                .build();
    }
}