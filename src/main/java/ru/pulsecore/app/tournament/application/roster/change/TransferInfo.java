package ru.pulsecore.app.tournament.application.roster.change;

import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;

import java.util.List;
import java.util.Objects;

public record TransferInfo(
        TournamentDto from,
        TournamentDto to,
        boolean timeChanged,
        boolean dateChanged,
        boolean leagueChanged,
        boolean hallChanged,
        boolean playersChanged
) {

    public static TransferInfo of(TournamentDto from, TournamentDto to) {
        boolean leagueChanged = from.getLeague() != null
                && to.getLeague() != null
                && !Objects.equals(from.getLeague(), to.getLeague());

        return new TransferInfo(
                from,
                to,
                !Objects.equals(parseTime(from), parseTime(to)),
                !Objects.equals(parseDate(from), parseDate(to)),
                leagueChanged,
                !Objects.equals(extractHall(from), extractHall(to)),
                !Objects.equals(extractPlayers(from), extractPlayers(to))
        );
    }

    private static List<String> extractPlayers(TournamentDto t) {
        if (t.getPlayers() == null) return List.of();
        return t.getPlayers().stream()
                .sorted()
                .toList();
    }

    public boolean hasAnyChange() {
        return timeChanged || dateChanged || leagueChanged || hallChanged;
    }

    private static String parseTime(TournamentDto t) {
        return t.getDate() != null ? DateTimeUtils.parseTime(t.getDate().getDate()) : null;
    }

    private static java.time.LocalDate parseDate(TournamentDto t) {
        return t.getDate() != null ? DateTimeUtils.parseDate(t.getDate().getDate()) : null;
    }

    private static Integer extractHall(TournamentDto t) {
        return NumberUtils.extractInt(t.getHall());
    }
}