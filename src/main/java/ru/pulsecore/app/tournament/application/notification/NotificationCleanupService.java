package ru.pulsecore.app.tournament.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupService {


    private final PlayerNotificationRepository notificationRepository;
    private static final int DAYS_TO_KEEP = 3;

    @Transactional
    public void cleanup() {
        try {
            LocalDate thresholdDate = LocalDate.now().minusDays(DAYS_TO_KEEP);
            notificationRepository.deleteByTournament_FinishedTrueAndTournament_DateBefore(thresholdDate);
            log.info("Турниры имеющие статус завершенных были успешно удалены {} ",thresholdDate);
        } catch (Exception e){
            log.error("Не удалось очистить турниры: {} ",e.getMessage());
        }

    }
}
