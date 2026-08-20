package ru.pulsecore.app.tournament.application.roster.change.remove;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pulsecore.app.shared.dto.response.DateDto;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerReplacementServiceTest {

    @Mock
    private PlayerChangeNotificationPublisher notificationPublisher;

    @Mock
    private PlayerNotificationRepository notificationRepository;

    @Mock
    private PlayerTransferDetector transferDetector;

    @Mock
    private PlayerRemovalDetector playerRemovalDetector;

    @Mock
    private PlayerNotificationCreator playerNotificationCreator;

    @InjectMocks
    private PlayerReplacementService service;

    private PlayerData player(String name) {
        return new PlayerData(
                UUID.randomUUID(),
                name,
                name.replace(" ", ".") + "@example.com",
                null,
                true,
                true,
                true,
                null,
                null
        );
    }

    private TournamentDto tournament(Long id, String... players) {
        TournamentDto dto = new TournamentDto();
        dto.setId(id);
        dto.setTitle("Лига С");
        dto.setPlayers(List.of(players));
        dto.setHall("Зал 10");

        DateDto dateDto = new DateDto();
        dateDto.setDate("2026-08-19 12:00:00.000000");
        dto.setDate(dateDto);

        return dto;
    }

    private TransferInfo transferInfo(TournamentDto from, TournamentDto to) {
        return TransferInfo.of(from, to);
    }

    @Test
    void shouldReturnFalseWhenNoPlayersRemoved() {
        List<PlayerData> oldPlayers = List.of(player("Иванов Иван"));
        TournamentDto newTournament = tournament(1L, "Иванов Иван");

        when(playerRemovalDetector.findRemovedNames(oldPlayers, newTournament))
                .thenReturn(List.of());

        boolean result = service.processReplacement(
                oldPlayers, newTournament, 1L, Map.of());

        assertThat(result).isFalse();
        verify(notificationPublisher, never()).sendTransferNotification(any(), any());
        verify(notificationPublisher, never()).sendReplacementNotification(any(), any());
    }

    @Test
    void shouldProcessTransferWhenTransferFound() {
        PlayerData ivan = player("Иванов Иван");
        List<PlayerData> oldPlayers = List.of(ivan);
        TournamentDto newTournament = tournament(1L, "Петров Пётр");
        TournamentDto transferTo = tournament(2L, "Иванов Иван");
        TransferInfo transferInfo = transferInfo(newTournament, transferTo);

        when(playerRemovalDetector.findRemovedNames(oldPlayers, newTournament))
                .thenReturn(List.of("иванов иван"));
        when(playerRemovalDetector.findPlayerForReplace(List.of("иванов иван"), oldPlayers))
                .thenReturn(List.of(ivan));
        when(transferDetector.findTransfer(ivan, newTournament, Map.of()))
                .thenReturn(transferInfo);

        boolean result = service.processReplacement(
                oldPlayers, newTournament, 1L, Map.of());

        assertThat(result).isTrue();
        verify(notificationPublisher).sendTransferNotification(ivan, transferInfo);
        verify(playerNotificationCreator).createNotificationForTransfer(ivan, transferTo);
        verify(notificationPublisher, never()).sendReplacementNotification(any(), any());
        verify(notificationRepository).deleteByPlayerIdAndTournamentId(ivan.playerId(), 1L);
    }

    @Test
    void shouldProcessRemovalWhenNoTransferFound() {
        PlayerData ivan = player("Иванов Иван");
        List<PlayerData> oldPlayers = List.of(ivan);
        TournamentDto newTournament = tournament(1L, "Петров Пётр");

        when(playerRemovalDetector.findRemovedNames(oldPlayers, newTournament))
                .thenReturn(List.of("иванов иван"));
        when(playerRemovalDetector.findPlayerForReplace(List.of("иванов иван"), oldPlayers))
                .thenReturn(List.of(ivan));
        when(transferDetector.findTransfer(ivan, newTournament, Map.of()))
                .thenReturn(null);

        boolean result = service.processReplacement(
                oldPlayers, newTournament, 1L, Map.of());

        assertThat(result).isTrue();
        verify(notificationPublisher).sendReplacementNotification(ivan, newTournament);
        verify(notificationPublisher, never()).sendTransferNotification(any(), any());
        verify(playerNotificationCreator, never()).createNotificationForTransfer(any(), any());
        verify(notificationRepository).deleteByPlayerIdAndTournamentId(ivan.playerId(), 1L);
    }

    @Test
    void shouldProcessMultipleRemovedPlayers() {
        PlayerData ivan = player("Иванов Иван");
        PlayerData petr = player("Петров Пётр");
        List<PlayerData> oldPlayers = List.of(ivan, petr);
        TournamentDto newTournament = tournament(1L, "Сидоров Сидр");

        when(playerRemovalDetector.findRemovedNames(oldPlayers, newTournament))
                .thenReturn(List.of("иванов иван", "петров пётр"));
        when(playerRemovalDetector.findPlayerForReplace(
                List.of("иванов иван", "петров пётр"), oldPlayers))
                .thenReturn(List.of(ivan, petr));
        when(transferDetector.findTransfer(any(), any(), any()))
                .thenReturn(null);

        boolean result = service.processReplacement(
                oldPlayers, newTournament, 1L, Map.of());

        assertThat(result).isTrue();
        verify(notificationPublisher, times(2)).sendReplacementNotification(any(), any());
        verify(notificationRepository).deleteByPlayerIdAndTournamentId(ivan.playerId(), 1L);
        verify(notificationRepository).deleteByPlayerIdAndTournamentId(petr.playerId(), 1L);
    }
}