package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;

@Slf4j
@Service
public class JsonTournamentStatusParser {

    public TournamentStatus parseStatus(TournamentPage page) {
        if (page == null || page.raw() == null) return TournamentStatus.NOT_STARTED;

        JsonNode root = page.raw();

        if (isCancelled(root)) return TournamentStatus.CANCELLED;
        if (isFinished(root)) return TournamentStatus.FINISHED;
        if (isInProgress(root)) return TournamentStatus.IN_PROGRESS;
        return TournamentStatus.NOT_STARTED;
    }

    private boolean isCancelled(JsonNode root) {
        JsonNode games = root.path("games");

        String first = games.path(0).path("statusType").asText("");
        String second = games.path(1).path("statusType").asText("");

        return "canceled".equals(first) && "canceled".equals(second);
    }

    private boolean isFinished(JsonNode root) {
        for (JsonNode g : root.path("games")) {
            if (!"final".equals(g.path("groupType").asText(""))) continue;

            String status = g.path("statusType").asText("");
            boolean fin = "completed".equals(status) || "canceled".equals(status);
            if (fin) log.debug("FINISHED: финал statusType={}", status);
            return fin;
        }
        return false;
    }

    /**
     * Турнир начался, если есть goes ИЛИ хотя бы один матч с непустым счётом/сетами
     */
    private boolean isInProgress(JsonNode root) {
        for (JsonNode g : root.path("games")) {
            if ("goes".equals(g.path("statusType").asText(""))) {
                log.debug("IN_PROGRESS: матч идёт — gameId={}", g.path("gameId").asLong());
                return true;
            }
        }
        return false;
    }


}