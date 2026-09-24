package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;

@Slf4j
@Service
public class JsonTournamentStatusParser {

    public TournamentStatus parseStatus(Document doc) {
        JsonNode root = BootstrapJson.parse(doc);
        if (root == null) return TournamentStatus.NOT_STARTED;

        if (isCancelled(root)) return TournamentStatus.CANCELLED;
        if (isFinished(root)) return TournamentStatus.FINISHED;
        if (isInProgress(root)) return TournamentStatus.IN_PROGRESS;
        return TournamentStatus.NOT_STARTED;
    }

    /** Отменённый турнир. Признаки:
     *  1) все матчи statusType=canceled
     *  2) все игроки removed=true
     *  3) вообще нет игроков и матчей
     */
    private boolean isCancelled(JsonNode root) {
        JsonNode players = root.path("players");
        JsonNode games = root.path("games");

        int playersSize = players.isArray() ? players.size() : 0;
        int gamesSize = games.isArray() ? games.size() : 0;

        // 3) пусто и там, и там
        if (playersSize == 0 && gamesSize == 0) {
            log.debug("JsonTournamentStatusParser: CANCELLED — пустой players и games");
            return true;
        }

        // 2) все игроки removed=true
        if (playersSize > 0) {
            boolean allRemoved = true;
            for (JsonNode p : players) {
                if (!p.path("removed").asBoolean(false)) {
                    allRemoved = false;
                    break;
                }
            }
            if (allRemoved) {
                log.debug("JsonTournamentStatusParser: CANCELLED — все игроки removed=true");
                return true;
            }
        }

        // 1) все матчи canceled
        if (gamesSize > 0) {
            boolean allCanceled = true;
            for (JsonNode g : games) {
                String status = g.path("statusType").asText("");
                if (!"canceled".equals(status)) {
                    allCanceled = false;
                    break;
                }
            }
            if (allCanceled) {
                log.debug("JsonTournamentStatusParser: CANCELLED — все матчи canceled");
                return true;
            }
        }

        return false;
    }

    private boolean isFinished(JsonNode root) {
        for (JsonNode g : root.path("games")) {
            String groupType = g.path("groupType").asText("");
            if (!"final".equals(groupType)) continue;
            return "completed".equals(g.path("statusType").asText(""));
        }
        return false;
    }

    private boolean isInProgress(JsonNode root) {
        for (JsonNode g : root.path("games")) {
            if ("goes".equals(g.path("statusType").asText(""))) return true;
        }
        return false;
    }
}