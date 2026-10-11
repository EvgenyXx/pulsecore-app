package ru.pulsecore.app.tournament.application.roster.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dispetcher.PushDispatcher;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderNotificationSender {

    private static final ZoneId MSK = ZoneId.of("Europe/Moscow");

    private final PushDispatcher pushDispatcher;
    private final PlayerNotificationRepository notificationRepository;

    public void sendHourReminders(
            List<PlayerNotification> notifications,
            Map<UUID, PlayerData> playerMap) {

        if (notifications.isEmpty()) return;

        LocalTime nowMsk = LocalTime.now(MSK);
        Map<UUID, PushContent> pushContent = new HashMap<>();
        List<PlayerNotification> updated = new ArrayList<>();

        for (PlayerNotification pn : notifications) {
            if (!shouldSendHour(pn, playerMap, nowMsk)) continue;

            PlayerData player = playerMap.get(pn.getPlayerId());
            TournamentEntity t = pn.getTournament();

            pushContent.put(player.id(), new PushContent(
                    "Турнир через час",
                    PushMessageBuilder.buildHourReminderBody(t.getTime()),
                    t.getLink()
            ));

            pn.setPushReminderSent(true);
            updated.add(pn);
        }

        flush(pushContent, updated);
    }

    public void sendEveningReminders(
            List<PlayerNotification> notifications,
            Map<UUID, PlayerData> playerMap,
            LocalTime now) {

        if (notifications.isEmpty()) return;
        if (now.getHour() < 20) return;

        Map<UUID, PushContent> pushContent = new HashMap<>();
        List<PlayerNotification> updated = new ArrayList<>();

        for (PlayerNotification pn : notifications) {
            if (!shouldSendEvening(pn, playerMap)) continue;

            PlayerData player = playerMap.get(pn.getPlayerId());
            TournamentEntity t = pn.getTournament();

            pushContent.put(player.id(), new PushContent(
                    "Завтра турнир",
                    PushMessageBuilder.buildEveningReminderBody(t.getTime()),
                    t.getLink()
            ));

            pn.setPushEveningSent(true);
            updated.add(pn);
        }

        flush(pushContent, updated);
    }

    private boolean shouldSendHour(PlayerNotification pn, Map<UUID, PlayerData> playerMap, LocalTime nowMsk) {
        PlayerData player = playerMap.get(pn.getPlayerId());
        if (player == null || pn.isPushReminderSent()) return false;

        TournamentEntity t = pn.getTournament();
        if (t == null || t.getTime() == null || t.getTime().isEmpty()) return false;

        Long minutes = DateTimeUtils.parseMinutesUntil(t.getTime(), nowMsk);
        return minutes != null && minutes > 0 && minutes <= 60;
    }

    private boolean shouldSendEvening(PlayerNotification pn, Map<UUID, PlayerData> playerMap) {
        PlayerData player = playerMap.get(pn.getPlayerId());
        if (player == null || pn.isPushEveningSent()) return false;

        TournamentEntity t = pn.getTournament();
        return t != null && t.getTime() != null && !t.getTime().isEmpty();
    }

    private void flush(Map<UUID, PushContent> pushContent, List<PlayerNotification> updated) {
        if (!pushContent.isEmpty()) pushDispatcher.send(pushContent);
        if (!updated.isEmpty()) notificationRepository.saveAll(updated);
    }
}