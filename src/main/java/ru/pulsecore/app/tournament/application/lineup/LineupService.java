package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.application.mapping.LineupMapper;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.repository.LineupRepository;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LineupService {


    private final LineupRepository lineupRepository;
    private final PlayerClient playerClient;
    private final LineupMapper lineupMapper;


    public Map<String, List<LineupDto>> getAllGroupedByHall(LocalDate date) {
        List<Lineup> all = lineupRepository.findByDate(date);
        return groupByHall(all.stream()
                .map(lineupMapper::toDto).toList());
    }

    public Map<String, List<LineupDto>> getMyGroupedByHall(UUID playerId, LocalDate date) {
        PlayerData playerData = playerClient.getPlayerById(playerId);

        if (playerData.selectedHalls() == null || playerData.selectedHalls().isBlank()) {
            return Map.of();
        }


        List<String> halls = Arrays.asList(playerData.selectedHalls().split(",\\s*"));
        List<Lineup> filtered = getLineupsForHalls(date, halls);

        List<LineupDto> dtos = filtered.stream()
                .map(lineupMapper::toDto)
                .map(dto -> markPlayer(dto, playerData.name()))
                .toList();

        return groupByHall(dtos);
    }

    private List<Lineup> getLineupsForHalls(LocalDate date, List<String> halls) {
        if (halls == null || halls.isEmpty()) return List.of();
        return lineupRepository.findByDateAndHallIn(date, halls);
    }

    private LineupDto markPlayer(LineupDto dto, String playerName) {
        String searchName = NameNormalizer.normalizeForSearch(playerName);
        boolean isPlayer = dto.getPlayers() != null &&
                Arrays.stream(dto.getPlayers().split(","))
                        .map(String::trim)
                        .map(NameNormalizer::normalizeForSearch)
                        .anyMatch(p -> p.contains(searchName) || searchName.contains(p));
        dto.setPlayer(isPlayer);
        return dto;
    }


    private Map<String, List<LineupDto>> groupByHall(List<LineupDto> lineups) {
        return lineups.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getHall() != null ? l.getHall() : "Без зала",
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }
}