package ru.pulsecore.app.tournament.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.nodes.Document;

/**
 * Готовые данные турнира, вытащенные из страницы.
 * Immutable. Создаётся {@code JsonTournamentParser} один раз на турнир
 * и передаётся по сервисам как аргумент.
 */
public record TournamentPage(
        Document document,      // исходный HTML — для TextLeagueResolver
        JsonNode raw,           // сырой JSON — для глубокого обхода (games/players)
        Long id,
        String date,
        String time,            // "06:00"
        String hall,            // "№10"
        String leagueTitle,     // "A" / "Суперлига"
        String removedPlayer
) {}