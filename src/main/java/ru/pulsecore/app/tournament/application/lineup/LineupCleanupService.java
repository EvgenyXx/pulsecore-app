package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.ChatMessageRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.LineupRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineupCleanupService {

    private final LineupRepository lineupRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void cleanupOld() {
        LocalDate before = LocalDate.now().minusDays(30);
        List<Long> oldLineupIds = lineupRepository.findIdsByDateBefore(before);

        if (!oldLineupIds.isEmpty()) {
            chatMessageRepository.deleteByLineupIdIn(oldLineupIds);
            lineupRepository.deleteByDateBefore(before);
            log.info("Очищено {} старых составов", oldLineupIds.size());
        }
    }
}