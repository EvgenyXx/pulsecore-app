package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.util.RegexUtils;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.application.mapping.LineupMapper;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.repository.LineupRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
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

        List<String> halls = RegexUtils.CSV_SPLITTER.splitAsStream(playerData.selectedHalls()).toList();
        Pageable pageable = PageRequest.of(page, size);

        Page<Lineup> lineups = lineupRepository.findByDateAndHallIn(date, halls, pageable);

        return lineups.map(entity -> lineupMapper.toDto(entity, playerData.name()));
    }
}