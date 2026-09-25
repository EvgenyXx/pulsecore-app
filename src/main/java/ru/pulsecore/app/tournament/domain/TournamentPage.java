package ru.pulsecore.app.tournament.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.nodes.Document;

/**
 * Готовые данные турнира, вытащенные из страницы.
 * Immutable. Создаётся {@code JsonTournamentParser} один раз на турнир
 * и передаётся по сервисам как аргумент.
 */
public record TournamentPage(
        Document document,
        JsonNode raw,
        Long id,
        String date,
        String time,
        String hall,
        String leagueTitle,
        String typeId,
        String removedPlayer
) {
}