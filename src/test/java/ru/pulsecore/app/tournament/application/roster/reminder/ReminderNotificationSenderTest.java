package ru.pulsecore.app.tournament.application.roster.reminder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderNotificationSenderTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PlayerNotificationRepository notificationRepository;

    @InjectMocks
    private ReminderNotificationSender sender;

    private PlayerData player(boolean pushEnabled) {
        return new PlayerData(
                UUID.randomUUID(),
                "Иванов Иван",
                "ivan@example.com",
                null,
                pushEnabled,
                true,
                true,
                null,
                null
        );
    }

    private TournamentEntity tournament(String time) {
        TournamentEntity t = new TournamentEntity();
        t.setId(1L);
        t.setExternalId(3017203L);
        t.setDate(LocalDate.now());
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

    @Test
    void shouldSendHourReminderWhenTournamentInOneHour() {
        PlayerData player = player(true);
        TournamentEntity tournament = tournament(
                LocalTime.now(ZoneId.of("Europe/Moscow")).plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        PlayerNotification pn = notification(player, tournament);
        List<PlayerData> hourPushed = new ArrayList<>();

        sender.sendHourReminder(player, tournament.getTime(), hourPushed, pn);

        assertThat(hourPushed).contains(player);
        verify(publisher).publishEvent(any(PushNotificationEvent.class));
        verify(notificationRepository).save(pn);
    }

    @Test
    void shouldNotSendHourReminderWhenTournamentMoreThanOneHour() {
        PlayerData player = player(true);
        TournamentEntity tournament = tournament("23:59");
        PlayerNotification pn = notification(player, tournament);
        List<PlayerData> hourPushed = new ArrayList<>();

        sender.sendHourReminder(player, tournament.getTime(), hourPushed, pn);

        assertThat(hourPushed).isEmpty();
        verify(publisher, never()).publishEvent(any(PushNotificationEvent.class));
    }

    @Test
    void shouldNotSendHourReminderWhenPushDisabled() {
        PlayerData player = player(false);
        TournamentEntity tournament = tournament(
                LocalTime.now(ZoneId.of("Europe/Moscow")).plusMinutes(30).format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        PlayerNotification pn = notification(player, tournament);
        List<PlayerData> hourPushed = new ArrayList<>();

        sender.sendHourReminder(player, tournament.getTime(), hourPushed, pn);

        assertThat(hourPushed).isEmpty();
        verify(publisher, never()).publishEvent(any(PushNotificationEvent.class));
    }

    @Test
    void shouldSendEveningReminderAt20() {
        PlayerData player = player(true);
        TournamentEntity tournament = tournament("12:00");
        PlayerNotification pn = notification(player, tournament);
        List<PlayerData> eveningPushed = new ArrayList<>();

        sender.sendEveningReminder(player, pn, LocalTime.of(20, 0), eveningPushed);

        assertThat(eveningPushed).contains(player);
        verify(publisher).publishEvent(any(PushNotificationEvent.class));
    }

    @Test
    void shouldNotSendEveningReminderWhenNot20() {
        PlayerData player = player(true);
        TournamentEntity tournament = tournament("12:00");
        PlayerNotification pn = notification(player, tournament);
        List<PlayerData> eveningPushed = new ArrayList<>();

        sender.sendEveningReminder(player, pn, LocalTime.of(19, 0), eveningPushed);

        assertThat(eveningPushed).isEmpty();
        verify(publisher, never()).publishEvent(any(PushNotificationEvent.class));
    }
}