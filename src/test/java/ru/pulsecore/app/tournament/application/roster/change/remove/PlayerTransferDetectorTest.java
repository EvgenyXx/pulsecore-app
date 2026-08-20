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
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerTransferDetectorTest {

    @Mock
    private PlayerNotificationRepository notificationRepository;

    @InjectMocks
    private PlayerTransferDetector detector;

    private PlayerData player() {
        return new PlayerData(
                UUID.randomUUID(),
                "Иванов Иван",
                "Иванов Иван".replace(" ", ".") + "@example.com",
                null,
                true,
                true,
                true,
                null,
                null
        );
    }

    private TournamentDto tournament(Long id, String title, String... players) {
        TournamentDto dto = new TournamentDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setLink("https://masters-league.com/tours/tournament-" + id + "/");
        dto.setPlayers(List.of(players));
        dto.setHall("Зал 10");

        DateDto dateDto = new DateDto();
        dateDto.setDate("2026-08-19 12:00:00.000000");
        dto.setDate(dateDto);

        return dto;
    }

    @Test
    void shouldReturnNullWhenNoTransferFound() {
        PlayerData ivan = player();
        TournamentDto oldTournament = tournament(1L, "Лига С", "Петров Пётр");

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(
                        tournament(2L, "Лига D", "Сидоров Сидр")
                )
        );

        TransferInfo result = detector.findTransfer(ivan, oldTournament, all);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullWhenPlayerAlreadyLinkedToNewTournament() {
        PlayerData ivan = player();
        TournamentDto oldTournament = tournament(1L, "Лига С", "Петров Пётр");
        TournamentDto newTournament = tournament(2L, "Лига D", "Иванов Иван");

        TournamentEntity entity = new TournamentEntity();
        entity.setId(2L);

        PlayerNotification pn = PlayerNotification.builder()
                .playerId(ivan.playerId())
                .tournament(entity)
                .build();

        when(notificationRepository.findByTournamentLink(newTournament.getLink()))
                .thenReturn(List.of(pn));

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(newTournament)
        );

        TransferInfo result = detector.findTransfer(ivan, oldTournament, all);

        assertThat(result).isNull();
    }

    @Test
    void shouldFindTransferWhenPlayerInNewTournament() {
        PlayerData ivan = player();
        TournamentDto oldTournament = tournament(1L, "Лига С", "Петров Пётр");
        TournamentDto newTournament = tournament(2L, "Лига D", "Иванов Иван");

        when(notificationRepository.findByTournamentLink(newTournament.getLink()))
                .thenReturn(List.of());

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(newTournament)
        );

        TransferInfo result = detector.findTransfer(ivan, oldTournament, all);

        assertThat(result).isNotNull();
        assertThat(result.from()).isEqualTo(oldTournament);
        assertThat(result.to()).isEqualTo(newTournament);
    }

    @Test
    void shouldSkipOldTournamentWhenSearching() {
        PlayerData ivan = player();
        TournamentDto oldTournament = tournament(1L, "Лига С", "Иванов Иван");

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(oldTournament)
        );

        TransferInfo result = detector.findTransfer(ivan, oldTournament, all);

        assertThat(result).isNull();
    }

    @Test
    void shouldHandleMultipleDays() {
        PlayerData ivan = player();
        TournamentDto oldTournament = tournament(1L, "Лига С", "Петров Пётр");
        TournamentDto newTournament = tournament(2L, "Лига D", "Иванов Иван");

        when(notificationRepository.findByTournamentLink(newTournament.getLink()))
                .thenReturn(List.of());

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(tournament(3L, "Лига B", "Сидоров Сидр")),
                "2026-08-20", List.of(newTournament)
        );

        TransferInfo result = detector.findTransfer(ivan, oldTournament, all);

        assertThat(result).isNotNull();
        assertThat(result.to()).isEqualTo(newTournament);
    }
}