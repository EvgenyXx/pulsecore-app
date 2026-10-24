package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.application.mapping.LineupMapper;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.repository.LineupRepository;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LineupService {

    private final LineupRepository lineupRepository;
    private final PlayerClient playerClient;
    private final LineupMapper lineupMapper;

    @Transactional(readOnly = true)
    public List<String> getAvailableHalls(LocalDate date) {
        return lineupRepository.findDistinctHallsByDate(date);
    }

    @Transactional(readOnly = true)
    public Page<LineupDto> getMyPaged(UUID playerId, LocalDate date, int page, int size) {
        PlayerData playerData = playerClient.getPlayerById(playerId);

        if (playerData.selectedHalls() == null || playerData.selectedHalls().isBlank()) {
            return Page.empty(PageRequest.of(page, size));
        }

        List<String> halls = Arrays.asList(playerData.selectedHalls().split(",\\s*"));
        Pageable pageable = PageRequest.of(page, size);

        Page<Lineup> lineups = lineupRepository.findByDateAndHallIn(date, halls, pageable);

        return lineups.map(entity -> {
            LineupDto dto = lineupMapper.toDto(entity);
            boolean isPlayer = isPlayerInLineup(dto, playerData.name());
            dto.setPlayer(isPlayer);
            return dto;
        });
    }

    private boolean isPlayerInLineup(LineupDto dto, String playerName) {
        if (dto.getPlayers() == null || playerName == null) return false;

        String searchName = NameNormalizer.normalizeForSearch(playerName);
        return Arrays.stream(dto.getPlayers().split(","))
                .map(String::trim)
                .map(NameNormalizer::normalizeForSearch)
                .anyMatch(p -> p.contains(searchName) || searchName.contains(p));
    }
}