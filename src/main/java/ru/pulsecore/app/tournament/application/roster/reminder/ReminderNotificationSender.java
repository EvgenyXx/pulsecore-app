package ru.pulsecore.app.tournament.application.roster.reminder;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderNotificationSender {

    private final ApplicationEventPublisher publisher;
    private final PlayerNotificationRepository notificationRepository;

    public void sendHourReminder(
            PlayerData player,
            LocalTime now,
            List<PlayerData> hourPushed,
            PlayerNotification pn) {
        String time = pn.getTournament().getTime();
        if (time == null || time.isEmpty()) return;

        Long minutes = DateTimeUtils.parseMinutesUntil(time, now);
        if (minutes == null || minutes <= 0 || minutes > 60) return;

        if (player.pushEnabled()) {
            hourPushed.add(player);
            publisher.publishEvent(
                    new PushNotificationEvent(
                            player.playerId(),
                            "🏆 Турнир начинается!",
                            PushMessageBuilder.buildHourReminderBody(time, minutes),
                            "/dashboard"
                    )
            );
        }
        pn.setPushReminderSent(true);
        notificationRepository.save(pn);
    }

    public void sendEveningReminder(PlayerData player, PlayerNotification pn, LocalTime now, List<PlayerData> eveningPushed) {
        if (now.getHour() != 20 || pn.isPushEveningSent()) return;
        if (player.pushEnabled()) {
            eveningPushed.add(player);
            String time = pn.getTournament().getTime();
            publisher.publishEvent(
                    new PushNotificationEvent(
                            player.playerId(),
                            "📅 Завтра турнир!",
                            PushMessageBuilder.buildEveningReminderBody(time),
                            "/dashboard"
                    )
            );
        }
        pn.setPushEveningSent(true);
        notificationRepository.save(pn);
    }
}