package ru.pulsecore.app.tournament.application.roster.change;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.TournamentDataProvider;
import ru.pulsecore.app.tournament.application.roster.change.info.TournamentHashChecker;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentChangeServiceTest {

    @Mock
    private TournamentDataProvider tournamentDataProvider;

    @Mock
    private TournamentHashChecker tournamentHashChecker;

    @Mock
    private PlayerClient playerClient;

    @InjectMocks
    private TournamentChangeService service;

    private PlayerData player(String name, String email) {
        return new PlayerData(
                UUID.randomUUID(),
                name,
                email,
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
        dto.setHall("Зал 10");
        dto.setPlayers(List.of(players));
        return dto;
    }

    @Test
    void shouldSkipWhenNoActivePlayers() {
        when(playerClient.getAll()).thenReturn(List.of());

        service.checkChangedTournaments();

        verify(tournamentDataProvider, never()).getAllTournamentsFor3Days();
        verify(tournamentHashChecker, never()).checkAndUpdateHashes(any(), any());
    }

    @Test
    void shouldProcessPlayerAndCheckHashes() {
        PlayerData ivan = player("Иванов Иван", "ivan@example.com");
        when(playerClient.getAll()).thenReturn(List.of(ivan));

        TournamentDto tournament = tournament(1L, "Иванов Иван");
        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(tournament)
        );
        when(tournamentDataProvider.getAllTournamentsFor3Days()).thenReturn(all);

        service.checkChangedTournaments();

        verify(tournamentHashChecker).checkAndUpdateHashes(any(), eq(all));
    }

    @Test
    void shouldFilterByPlayerName() {
        PlayerData ivan = player("Иванов Иван", "ivan@example.com");
        PlayerData petr = player("Петров Пётр", "petr@example.com");
        when(playerClient.getAll()).thenReturn(List.of(ivan, petr));

        TournamentDto tournamentIvan = tournament(1L, "Иванов Иван");
        TournamentDto tournamentPetr = tournament(2L, "Петров Пётр");
        TournamentDto tournamentOther = tournament(3L, "Сидоров Сидр");

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(tournamentIvan, tournamentPetr, tournamentOther)
        );
        when(tournamentDataProvider.getAllTournamentsFor3Days()).thenReturn(all);

        service.checkChangedTournaments();

        verify(tournamentHashChecker).checkAndUpdateHashes(any(), eq(all));
    }

    @Test
    void shouldNotFailWhenTournamentHasNoPlayers() {
        PlayerData ivan = player("Иванов Иван", "ivan@example.com");
        when(playerClient.getAll()).thenReturn(List.of(ivan));

        TournamentDto emptyTournament = tournament(1L);
        emptyTournament.setPlayers(null);

        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(emptyTournament)
        );
        when(tournamentDataProvider.getAllTournamentsFor3Days()).thenReturn(all);

        service.checkChangedTournaments();

        verify(tournamentHashChecker).checkAndUpdateHashes(any(), eq(all));
    }

    @Test
    void shouldSetHallNumberWhenPlayerFound() {
        PlayerData ivan = player("Иванов Иван", "ivan@example.com");
        when(playerClient.getAll()).thenReturn(List.of(ivan));

        TournamentDto tournament = tournament(1L, "Иванов Иван");
        Map<String, List<TournamentDto>> all = Map.of(
                "2026-08-19", List.of(tournament)
        );
        when(tournamentDataProvider.getAllTournamentsFor3Days()).thenReturn(all);

        service.checkChangedTournaments();

        verify(tournamentHashChecker).checkAndUpdateHashes(
                argThat(result -> {
                    List<TournamentDto> tournaments = result.get("Иванов Иван");
                    return tournaments != null
                            && tournaments.size() == 1
                            && tournaments.get(0).getHallNumber() == 10;
                }),
                eq(all)
        );
    }
}