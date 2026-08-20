package ru.pulsecore.app.tournament.application.roster.reminder;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.time.ZoneId;
import java.util.List;

@Service
public class VladivostokReminderService extends RegionalReminderService {

    public VladivostokReminderService(PlayerNotificationRepository notificationRepository,
                                      PlayerClient playerClient,
                                      ReminderNotificationSender notificationSender) {
        super(notificationRepository, playerClient, notificationSender);
    }

    @Override
    protected List<Integer> getHalls() {
        return List.of(5);
    }

    @Override
    protected ZoneId getZone() {
        return ZoneId.of("Asia/Vladivostok");
    }

    @Override
    protected String getRegionName() {
        return "Владивосток";
    }
}