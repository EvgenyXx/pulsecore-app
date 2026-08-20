package ru.pulsecore.app.tournament.application.roster.change.remove;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.PlayerReplacedContext;
import ru.pulsecore.app.notification.application.mail.context.PlayerTransferredContext;
import ru.pulsecore.app.shared.dto.response.DateDto;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerChangeNotificationPublisherTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PlayerChangeNotificationPublisher publisher;

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

    private TournamentDto tournament() {
        TournamentDto dto = new TournamentDto();
        dto.setId(1L);
        dto.setTitle("Лига С");
        dto.setHall("Зал 10");
        dto.setLeague("Лига С");

        DateDto dateDto = new DateDto();
        dateDto.setDate("2026-08-19 12:00:00.000000");
        dto.setDate(dateDto);

        return dto;
    }

    @Test
    void shouldPublishReplacementNotification() {
        PlayerData player = player();
        TournamentDto tournament = tournament();

        publisher.sendReplacementNotification(player, tournament);

        ArgumentCaptor<MailNotificationEvent> captor = ArgumentCaptor.forClass(MailNotificationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        MailNotificationEvent event = captor.getValue();
        assertThat(event.getEmailType()).isEqualTo(MailTypes.PLAYER_REPLACED);
        assertThat(event.getContextMessage()).isInstanceOf(PlayerReplacedContext.class);

        PlayerReplacedContext context = (PlayerReplacedContext) event.getContextMessage();
        assertThat(context.to()).isEqualTo("ivan@example.com");
        assertThat(context.firstName()).isEqualTo("Иван");
        assertThat(context.tournamentTitle()).isEqualTo("Лига С");
        assertThat(context.hall()).isEqualTo("Зал 10");
        assertThat(context.league()).isEqualTo("Лига С");
    }

    @Test
    void shouldPublishTransferNotification() {
        PlayerData player = player();
        TournamentDto from = tournament();
        TournamentDto to = tournament();
        to.setId(2L);
        to.setTitle("Лига D");

        TransferInfo transferInfo = TransferInfo.of(from, to);

        publisher.sendTransferNotification(player, transferInfo);

        ArgumentCaptor<MailNotificationEvent> captor = ArgumentCaptor.forClass(MailNotificationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        MailNotificationEvent event = captor.getValue();
        assertThat(event.getEmailType()).isEqualTo(MailTypes.PLAYER_TRANSFERRED);
        assertThat(event.getContextMessage()).isInstanceOf(PlayerTransferredContext.class);

        PlayerTransferredContext context = (PlayerTransferredContext) event.getContextMessage();
        assertThat(context.to()).isEqualTo("ivan@example.com");
        assertThat(context.firstName()).isEqualTo("Иван");
        assertThat(context.transferInfo()).isEqualTo(transferInfo);
    }

    @Test
    void shouldHandleNullHallAndLeague() {
        PlayerData player = player();
        TournamentDto tournament = tournament();
        tournament.setHall(null);
        tournament.setLeague(null);

        publisher.sendReplacementNotification(player, tournament);

        ArgumentCaptor<MailNotificationEvent> captor = ArgumentCaptor.forClass(MailNotificationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        MailNotificationEvent event = captor.getValue();
        PlayerReplacedContext context = (PlayerReplacedContext) event.getContextMessage();

        assertThat(context.hall()).isEqualTo("—");
        assertThat(context.league()).isEqualTo("—");
    }
}