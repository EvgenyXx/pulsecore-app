package ru.pulsecore.app.tournament.application.roster.reminder;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import java.time.ZoneId;
import java.util.List;

@Service
public class OrenburgReminderService extends RegionalReminderService {

    public OrenburgReminderService(PlayerNotificationRepository notificationRepository,
                                   PlayerClient playerClient,
                                   ReminderNotificationSender notificationSender) {
        super(notificationRepository, playerClient, notificationSender);
    }

    @Override
    protected List<Integer> getHalls() {
        return List.of(20, 21);
    }

    @Override
    protected ZoneId getZone() {
        return ZoneId.of("Asia/Yekaterinburg");
    }

    @Override
    protected String getRegionName() {
        return "Оренбург";
    }
}