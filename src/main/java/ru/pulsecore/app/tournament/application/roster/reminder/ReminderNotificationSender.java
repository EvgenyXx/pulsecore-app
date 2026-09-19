package ru.pulsecore.app.tournament.application.roster.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderNotificationSender {

    private final ApplicationEventPublisher publisher;
    private final PlayerNotificationRepository notificationRepository;

    public void sendHourReminder(
            PlayerData player,
            String tournamentTime,
            List<PlayerData> hourPushed,
            PlayerNotification pn) {


        if (pn.isPushReminderSent()) return;

        if (tournamentTime == null || tournamentTime.isEmpty()) return;

        LocalTime nowMsk = LocalTime.now(ZoneId.of("Europe/Moscow"));
        Long minutes = DateTimeUtils.parseMinutesUntil(tournamentTime, nowMsk);

        if (minutes == null || minutes <= 0 || minutes > 60) return;

        if (player.pushEnabled()) {
            hourPushed.add(player);
            try {
                publisher.publishEvent(
                        new PushNotificationEvent(
                                player.playerId(),
                                "🏆 Турнир начинается!",
                                PushMessageBuilder.buildHourReminderBody(tournamentTime, minutes),
                                "/dashboard"
                        )
                );
            } catch (Exception e) {
                log.error("Hour push failed for {}: {}", player.playerId(), e.getMessage());
            }
        }

        pn.setPushReminderSent(true);
        notificationRepository.save(pn);
    }

    public void sendEveningReminder(PlayerData player, PlayerNotification pn, LocalTime now, List<PlayerData> eveningPushed) {

        if (pn.isPushEveningSent()) return;
        if (now.getHour() < 20) return;

        if (player.pushEnabled()) {
            eveningPushed.add(player);
            String time = pn.getTournament().getTime();
            try {
                publisher.publishEvent(
                        new PushNotificationEvent(
                                player.playerId(),
                                "📅 Завтра турнир!",
                                PushMessageBuilder.buildEveningReminderBody(time),
                                "/dashboard"
                        )
                );
            } catch (Exception e) {
                log.error("Evening push failed for {}: {}", player.playerId(), e.getMessage());
            }
        }

        pn.setPushEveningSent(true);
        notificationRepository.save(pn);
    }
}