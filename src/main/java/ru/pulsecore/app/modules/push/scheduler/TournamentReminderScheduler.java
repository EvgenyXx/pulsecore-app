package ru.pulsecore.app.modules.push.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.notification.repository.PlayerNotificationRepository;
import ru.pulsecore.app.modules.notification.service.NotificationPermissionService;
import ru.pulsecore.app.modules.push.service.WebPushService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentReminderScheduler {

    private final PlayerNotificationRepository notificationRepository;
    private final WebPushService webPushService;
    private final NotificationPermissionService notificationPermissionService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void sendTournamentReminders() {
        List<PlayerNotification> pending = notificationRepository.findPendingWithTournament();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        for (PlayerNotification pn : pending) {
            var tournament = pn.getTournament();
            if (tournament == null || tournament.getDate() == null) continue;

            LocalDate date = tournament.getDate();
            String time = tournament.getTime();
            var player = pn.getPlayer();

            if (date.equals(today) && time != null && !time.isEmpty()) {
                try {
                    LocalTime tournamentTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
                    long minutesUntilTournament = java.time.Duration.between(now, tournamentTime).toMinutes();

                    if (minutesUntilTournament > 0 && minutesUntilTournament <= 60) {
                        if (notificationPermissionService.canSendPush(player)) {
                            webPushService.sendToPlayer(
                                    player.getId(),
                                    "🏆 Турнир начинается!",
                                    "Начало в " + time + ". До старта " + minutesUntilTournament + " мин. Проверьте состав!\n\nPulseCore",
                                    "/dashboard"
                            );
                        }
                        pn.setPushReminderSent(true);
                        notificationRepository.save(pn);
                    }
                } catch (Exception e) {
                    log.warn("Ошибка парсинга времени турнира: {}", time);
                }
            }

            if (date.equals(tomorrow) && now.getHour() == 20 && !pn.isPushEveningSent()) {
                if (notificationPermissionService.canSendPush(player)) {
                    webPushService.sendToPlayer(
                            player.getId(),
                            "📅 Завтра турнир!",
                            "Завтра в " + (time != null ? time : "?") + ". Проверьте состав и будьте готовы!",
                            "/dashboard"
                    );
                }
                pn.setPushEveningSent(true);
                notificationRepository.save(pn);
            }
        }
    }
}