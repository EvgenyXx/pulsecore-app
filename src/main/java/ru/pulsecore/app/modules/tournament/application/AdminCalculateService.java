package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.ResultDto;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.tournament.api.dto.AdminCalculateResponse;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.service.TournamentSearchService;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCalculateService {

    private final TournamentSearchService tournamentSearchService;
    private final ResultService resultService;
    private final NameNormalizer nameNormalizer;  // 🔥 ДОБАВЛЕНО

    public AdminCalculateResponse calculate(String name, String startDate, String endDate) {

        if (endDate == null || endDate.isBlank()) {
            endDate = startDate;
        }

        List<TournamentDto> tournaments = tournamentSearchService
                .findByDateRangeAndPlayer(startDate, endDate, name);

        if (tournaments.isEmpty()) {
            return AdminCalculateResponse.builder()
                    .playerName(name)
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalAmount(0)
                    .tournamentCount(0)
                    .tournaments(List.of())
                    .build();
        }

        List<AdminCalculateResponse.TournamentResultItem> items = new ArrayList<>();
        double totalAmount = 0;

        // 🔥 Нормализуем поисковое имя через NameNormalizer
        String searchName = nameNormalizer.normalizeForSearch(name);

        for (TournamentDto t : tournaments) {
            try {
                ParsedResult parsed = resultService.calculateAll(t.getLink());

                // 🔥 Нормализуем имя игрока из результатов через NameNormalizer
                double playerAmount = parsed.results().stream()
                        .filter(r -> nameNormalizer.normalizeForSearch(r.getPlayer()).contains(searchName))
                        .mapToDouble(ResultDto::getTotal)
                        .sum();

                if (playerAmount > 0) {
                    totalAmount += playerAmount;
                    items.add(AdminCalculateResponse.TournamentResultItem.builder()
                            .date(t.getDate() != null ? t.getDate().getDate() : "—")
                            .amount(playerAmount)
                            .tournamentTitle(t.getTitle())
                            .tournamentId(t.getId())
                            .hasRemoved(parsed.hasRemoved())
                            .build());
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
}