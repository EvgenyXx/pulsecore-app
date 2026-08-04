package ru.pulsecore.app.modules.tournament_module.infrastructure.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament_module.application.notification.NotificationCleanupService;

// 🧹 Чистит старые завершенные турниры
// Каждый день в 03:00 удаляет:
// - finished = true
// - дата турнира старше 7 дней
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationCleanupService cleanupService;

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanup() {

        cleanupService.cleanup();
    }
}