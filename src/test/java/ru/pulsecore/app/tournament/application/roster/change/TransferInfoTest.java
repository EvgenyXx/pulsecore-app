package ru.pulsecore.app.tournament.application.roster.change;

import org.junit.jupiter.api.Test;
import ru.pulsecore.app.shared.dto.response.DateDto;
import ru.pulsecore.app.shared.dto.response.TournamentDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransferInfoTest {

    private TournamentDto tournament(
            String date,
            String hall,
            String league,
            String... players) {

        TournamentDto dto = new TournamentDto();
        dto.setId(1L);
        dto.setTitle("Лига С");
        dto.setHall(hall);
        dto.setLeague(league);
        dto.setPlayers(List.of(players));

        DateDto dateDto = new DateDto();
        dateDto.setDate(date);
        dto.setDate(dateDto);

        return dto;
    }

    @Test
    void shouldHandleNullDate() {
        TournamentDto from = tournament(String.valueOf(1L), "Лига С", "2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");
        TournamentDto to = tournament(String.valueOf(1L), "Лига С", null, "Зал 10", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.timeChanged()).isTrue();
        assertThat(info.dateChanged()).isTrue();
    }

    @Test
    void shouldDetectNoChanges() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");
        TournamentDto to = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.timeChanged()).isFalse();
        assertThat(info.dateChanged()).isFalse();
        assertThat(info.leagueChanged()).isFalse();
        assertThat(info.hallChanged()).isFalse();
        assertThat(info.playersChanged()).isFalse();
        assertThat(info.hasAnyChange()).isFalse();
    }

    @Test
    void shouldDetectTimeChanged() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");
        TournamentDto to = tournament("2026-08-19 13:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.timeChanged()).isTrue();
        assertThat(info.hasAnyChange()).isTrue();
    }

    @Test
    void shouldDetectDateChanged() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");
        TournamentDto to = tournament("2026-08-20 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.dateChanged()).isTrue();
        assertThat(info.hasAnyChange()).isTrue();
    }

    @Test
    void shouldDetectHallChanged() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");
        TournamentDto to = tournament("2026-08-19 12:00:00.000000", "Зал 5", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.hallChanged()).isTrue();
        assertThat(info.hasAnyChange()).isTrue();
    }

    @Test
    void shouldDetectLeagueChanged() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");
        TournamentDto to = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига D", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.leagueChanged()).isTrue();
        assertThat(info.hasAnyChange()).isTrue();
    }

    @Test
    void shouldDetectPlayersChanged() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван", "Петров Пётр");
        TournamentDto to = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.playersChanged()).isTrue();
    }

    @Test
    void shouldHandleNullLeague() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", "Зал 10", null, "Иванов Иван");
        TournamentDto to = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига D", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.leagueChanged()).isFalse();
    }

    @Test
    void shouldHandleNullHall() {
        TournamentDto from = tournament("2026-08-19 12:00:00.000000", null, "Лига С", "Иванов Иван");
        TournamentDto to = tournament("2026-08-19 12:00:00.000000", "Зал 10", "Лига С", "Иванов Иван");

        TransferInfo info = TransferInfo.of(from, to);

        assertThat(info.hallChanged()).isTrue();
    }
}