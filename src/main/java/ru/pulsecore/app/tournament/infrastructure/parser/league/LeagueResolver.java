package ru.pulsecore.app.tournament.infrastructure.parser.league;

import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;

/**
 * Стратегия определения лиги турнира.
 * Реализации вызываются по очереди в порядке {@link #order()} (меньше — раньше),
 * пока одна из них не вернёт не-null.
 */
public interface LeagueResolver {

    /**
     * @return определённая лига или {@code null}, если этот резолвер не смог определить
     */
    LeagueType resolve(TournamentPage page);

    /**
     * Приоритет: меньшее значение — более высокий приоритет.
     * JSON — 10, текст — 100.
     */
    default int order() {
        return Integer.MAX_VALUE;
    }
}