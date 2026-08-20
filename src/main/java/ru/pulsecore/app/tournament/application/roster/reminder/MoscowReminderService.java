package ru.pulsecore.app.tournament.application.roster.reminder;


import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import java.time.ZoneId;
import java.util.List;

@Service
public class MoscowReminderService extends RegionalReminderService {

    public MoscowReminderService(PlayerNotificationRepository notificationRepository,
                                 PlayerClient playerClient,
                                 ReminderNotificationSender notificationSender) {
        super(notificationRepository, playerClient, notificationSender);
    }

    @Override
    protected List<Integer> getHalls() {
        return List.of(1, 2, 3, 4, 6, 7, 8, 9, 10, 11);
    }

    @Override
    protected ZoneId getZone() {
        return ZoneId.of("Europe/Moscow");
    }

    @Override
    protected String getRegionName() {
        return "Москва";
    }
}