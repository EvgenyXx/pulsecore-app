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

        pending.forEach(pn -> processNotification(pn, today, tomorrow, now));
    }

    private void processNotification(PlayerNotification pn, LocalDate today, LocalDate tomorrow, LocalTime now) {
        var tournament = pn.getTournament();
        if (tournament == null || tournament.getDate() == null) return;

        if (tournament.getDate().equals(today)) {
            sendHourReminder(pn, now);
        }
        if (tournament.getDate().equals(tomorrow)) {
            sendEveningReminder(pn, now);
        }
    }

    private void sendHourReminder(PlayerNotification pn, LocalTime now) {
        String time = pn.getTournament().getTime();
        if (time == null || time.isEmpty()) return;

        Long minutes = parseMinutesUntil(time, now);
        if (minutes == null || minutes <= 0 || minutes > 60) return;

        pushHourNotification(pn, time, minutes);
        pn.setPushReminderSent(true);
        notificationRepository.save(pn);
    }

    private Long parseMinutesUntil(String time, LocalTime now) {
        try {
            LocalTime tournamentTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return java.time.Duration.between(now, tournamentTime).toMinutes();
        } catch (Exception e) {
            log.warn("Ошибка парсинга времени турнира: {}", time);
            return null;
        }
    }

    private void pushHourNotification(PlayerNotification pn, String time, long minutes) {
        if (!notificationPermissionService.canSendPush(pn.getPlayer())) return;
        webPushService.sendToPlayer(
                pn.getPlayer().getId(),
                "🏆 Турнир начинается!",
                "Начало в " + time + ". До старта " + minutes + " мин. Проверьте состав!\n\nPulseCore",
                "/dashboard"
        );
    }

    private void sendEveningReminder(PlayerNotification pn, LocalTime now) {
        if (now.getHour() != 20 || pn.isPushEveningSent()) return;

        if (notificationPermissionService.canSendPush(pn.getPlayer())) {
            String time = pn.getTournament().getTime();
            webPushService.sendToPlayer(
                    pn.getPlayer().getId(),
                    "📅 Завтра турнир!",
                    "Завтра в " + (time != null ? time : "?") + ". Проверьте состав и будьте готовы!",
                    "/dashboard"
            );
        }
        pn.setPushEveningSent(true);
        notificationRepository.save(pn);
    }
}