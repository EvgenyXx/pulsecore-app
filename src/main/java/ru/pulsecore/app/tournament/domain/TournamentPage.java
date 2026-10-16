package ru.pulsecore.app.tournament.domain;


import org.jsoup.nodes.Document;
import ru.pulsecore.app.tournament.api.dto.TournamentJson;

/**
 * Готовые данные турнира, вытащенные из страницы.
 * Immutable. Создаётся {@code JsonTournamentParser} один раз на турнир
 * и передаётся по сервисам как аргумент.
 */
public record TournamentPage(
        Document document,
        TournamentJson json
) {}