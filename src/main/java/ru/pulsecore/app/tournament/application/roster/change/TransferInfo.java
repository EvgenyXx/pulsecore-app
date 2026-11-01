package ru.pulsecore.app.tournament.application.roster.change;

import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


/**
 * DTO, описывающее перенос игрока из одного турнира в другой.
 *
 * <p>Создается при парсинге, когда игрок исчезает из старого турнира и появляется в новом.
 * Содержит данные обоих турниров (from/to) и флаги изменений (время, дата, зал и т.д.).</p>
 *
 * <p>Используется для формирования письма игроку (шаблон "Было/Стало/Изменения")
 * в {@link ru.pulsecore.app.tournament.application.roster.change.remove.PlayerChangeNotificationPublisher}.</p>
 */
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


    private static String parseTime(TournamentDto tournament) {
        if (tournament.getDate() == null) {
            return null;
        }
        return DateTimeUtils.parseTime(tournament.getDate().getDate());
    }

    private static LocalDate parseDate(TournamentDto tournament) {
        if (tournament.getDate() == null) {
            return null;
        }
        return DateTimeUtils.parseDate(tournament.getDate().getDate());
    }

    private static Integer extractHall(TournamentDto t) {
        return NumberUtils.extractInt(t.getHall());
    }
}