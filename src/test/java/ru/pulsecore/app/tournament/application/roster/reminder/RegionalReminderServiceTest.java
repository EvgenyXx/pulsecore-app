package ru.pulsecore.app.tournament.application.roster.reminder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalReminderServiceTest {

    @Mock
    private PlayerNotificationRepository notificationRepository;

    @Mock
    private PlayerClient playerClient;

    @Mock
    private ReminderNotificationSender reminderNotificationSender;

    private TestRegionalReminderService service;

    private PlayerData player() {
        return new PlayerData(
                UUID.randomUUID(),
                "Иванов Иван",
                "ivan@example.com",
                null,
                true,
                true,
                true,
                null,
                null
        );
    }

    private TournamentEntity tournament(LocalDate date, String time) {
        TournamentEntity t = new TournamentEntity();
        t.setId(1L);
        t.setExternalId(3017203L);
        t.setDate(date);
        t.setTime(time);
        return t;
    }

    private PlayerNotification notification(PlayerData player, TournamentEntity tournament) {
        PlayerNotification pn = PlayerNotification.builder()
                .playerId(player.playerId())
                .tournament(tournament)
                .build();
        return pn;
    }

    // Тестовый подкласс
    class TestRegionalReminderService extends RegionalReminderService {
        public TestRegionalReminderService(
                PlayerNotificationRepository repo,
                PlayerClient client,
                ReminderNotificationSender sender) {
            super(repo, client, sender);
        }

        @Override
        protected List<Integer> getHalls() {
            return List.of(10, 11);
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

    @Test
    void shouldSendHourReminderForTodayTournament() {
        service = new TestRegionalReminderService(notificationRepository, playerClient, reminderNotificationSender);

        PlayerData player = player();
        TournamentEntity tournament = tournament(LocalDate.now(ZoneId.of("Europe/Moscow")), "12:00");
        PlayerNotification pn = notification(player, tournament);

        when(notificationRepository.findPendingByHalls(anyList())).thenReturn(List.of(pn));
        when(playerClient.getPlayerDataByIds(anySet())).thenReturn(List.of(player));

        service.sendReminders();

        verify(reminderNotificationSender).sendHourReminder(eq(player), eq("12:00"), anyList(), eq(pn));
    }

    @Test
    void shouldSendEveningReminderForTomorrowTournament() {
        service = new TestRegionalReminderService(notificationRepository, playerClient, reminderNotificationSender);

        PlayerData player = player();
        LocalDate tomorrow = LocalDate.now(ZoneId.of("Europe/Moscow")).plusDays(1);
        TournamentEntity tournament = tournament(tomorrow, "12:00");
        PlayerNotification pn = notification(player, tournament);

        when(notificationRepository.findPendingByHalls(anyList())).thenReturn(List.of(pn));
        when(playerClient.getPlayerDataByIds(anySet())).thenReturn(List.of(player));

        service.sendReminders();

        verify(reminderNotificationSender).sendEveningReminder(eq(player), eq(pn), any(), anyList());
    }

    @Test
    void shouldNotSendWhenNoNotifications() {
        service = new TestRegionalReminderService(notificationRepository, playerClient, reminderNotificationSender);

        when(notificationRepository.findPendingByHalls(anyList())).thenReturn(List.of());

        service.sendReminders();

        verify(reminderNotificationSender, never()).sendHourReminder(any(), any(), any(), any());
        verify(reminderNotificationSender, never()).sendEveningReminder(any(), any(), any(), any());
    }
}