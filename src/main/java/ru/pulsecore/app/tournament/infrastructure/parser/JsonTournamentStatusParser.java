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

    private boolean isCancelled(JsonNode root) {
        JsonNode players = root.path("players");
        JsonNode games = root.path("games");

        int playersSize = players.isArray() ? players.size() : 0;
        int gamesSize = games.isArray() ? games.size() : 0;

        if (playersSize == 0 && gamesSize == 0) {
            log.debug("CANCELLED: пустой players и games");
            return true;
        }

        if (playersSize > 0) {
            boolean allRemoved = true;
            for (JsonNode p : players) {
                if (!p.path("removed").asBoolean(false)) { allRemoved = false; break; }
            }
            if (allRemoved) {
                log.debug("CANCELLED: все игроки removed=true");
                return true;
            }
        }

        if (gamesSize > 0) {
            boolean allCanceled = true;
            for (JsonNode g : games) {
                if (!"canceled".equals(g.path("statusType").asText(""))) {
                    allCanceled = false; break;
                }
            }
            if (allCanceled) {
                log.debug("CANCELLED: все матчи canceled");
                return true;
            }
        }

        return false;
    }

    private boolean isFinished(JsonNode root) {
        for (JsonNode g : root.path("games")) {
            String groupType = g.path("groupType").asText("");
            if (!"final".equals(groupType)) continue;

            String status = g.path("statusType").asText("");
            boolean fin = "completed".equals(status) || "canceled".equals(status);
            if (fin) log.debug("FINISHED: финал statusType={}", status);
            return fin;
        }
        return false;
    }

    /** Турнир начался, если есть goes ИЛИ хотя бы один матч с непустым счётом/сетами */
    private boolean isInProgress(JsonNode root) {
        for (JsonNode g : root.path("games")) {
            String status = g.path("statusType").asText("");

            if ("goes".equals(status)) {
                log.debug("IN_PROGRESS: матч идёт — gameId={}", g.path("gameId").asLong());
                return true;
            }

            if (isMatchPlayed(g)) {
                log.debug("IN_PROGRESS: матч сыгран — gameId={}, score={}",
                        g.path("gameId").asLong(), g.path("score"));
                return true;
            }
        }
        return false;
    }

    /** Матч сыгран: score != [0,0] ИЛИ setsStr непустой */
    private boolean isMatchPlayed(JsonNode g) {
        JsonNode score = g.path("score");
        if (score.isArray() && score.size() >= 2) {
            int s1 = score.get(0).asInt(0);
            int s2 = score.get(1).asInt(0);
            if (s1 > 0 || s2 > 0) return true;
        }
        String setsStr = g.path("setsStr").asText("");
        return setsStr != null && !setsStr.isBlank();
    }
}