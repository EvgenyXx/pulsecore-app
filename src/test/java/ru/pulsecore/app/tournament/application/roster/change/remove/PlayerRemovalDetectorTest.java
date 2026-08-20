package ru.pulsecore.app.tournament.application.roster.change.remove;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PlayerRemovalDetectorTest {

    @InjectMocks
    private PlayerRemovalDetector detector;

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

    private TournamentDto tournament(List<String> players) {
        TournamentDto dto = new TournamentDto();
        dto.setPlayers(players);
        return dto;
    }

    @Test
    void shouldFindRemovedPlayerNames() {
        List<PlayerData> oldPlayers = List.of(
                player("Иванов Иван"),
                player("Петров Пётр"),
                player("Сидоров Сидр")
        );
        TournamentDto newTournament = tournament(List.of(
                "Иванов Иван",
                "Петров Пётр"
        ));

        List<String> removed = detector.findRemovedNames(oldPlayers, newTournament);

        assertThat(removed).containsExactly("сидоров сидр");
    }

    @Test
    void shouldReturnEmptyWhenNoPlayersRemoved() {
        List<PlayerData> oldPlayers = List.of(
                player("Иванов Иван"),
                player("Петров Пётр")
        );
        TournamentDto newTournament = tournament(List.of(
                "Иванов Иван",
                "Петров Пётр"
        ));

        List<String> removed = detector.findRemovedNames(oldPlayers, newTournament);

        assertThat(removed).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenAllPlayersRemoved() {
        List<PlayerData> oldPlayers = List.of(
                player("Иванов Иван"),
                player("Петров Пётр")
        );
        TournamentDto newTournament = tournament(List.of());

        List<String> removed = detector.findRemovedNames(oldPlayers, newTournament);

        assertThat(removed).containsExactly("иванов иван", "петров пётр");
    }

    @Test
    void shouldFindPlayerDataForRemovedNames() {
        PlayerData ivan = player("Иванов Иван");
        PlayerData petr = player("Петров Пётр");
        PlayerData sidor = player("Сидоров Сидр");

        List<String> removedNames = List.of("иванов иван", "сидоров сидр");
        List<PlayerData> allPlayers = List.of(ivan, petr, sidor);

        List<PlayerData> result = detector.findPlayerForReplace(removedNames, allPlayers);

        assertThat(result).containsExactly(ivan, sidor);
    }

    @Test
    void shouldNormalizeNamesIgnoringCase() {
        List<PlayerData> oldPlayers = List.of(
                player("Иванов Иван"),
                player("Петров Пётр")
        );
        TournamentDto newTournament = tournament(List.of(
                "иванов иван",
                "ПЕТРОВ ПЁТР"
        ));

        List<String> removed = detector.findRemovedNames(oldPlayers, newTournament);

        assertThat(removed).isEmpty();
    }
}