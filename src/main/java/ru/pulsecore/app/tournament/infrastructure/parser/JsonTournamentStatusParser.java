package ru.pulsecore.app.tournament.infrastructure.parser;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.TournamentJson;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.GameStage;
import ru.pulsecore.app.tournament.domain.enums.GameStatus;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;

import java.util.List;

@Slf4j
@Service
public class JsonTournamentStatusParser {

    public TournamentStatus parseStatus(TournamentPage page) {
        if (page == null || page.json() == null) return TournamentStatus.NOT_STARTED;

        TournamentJson json = page.json();

        if (isCancelled(json)) return TournamentStatus.CANCELLED;
        if (isFinished(json)) return TournamentStatus.FINISHED;
        if (isInProgress(json)) return TournamentStatus.IN_PROGRESS;
        return TournamentStatus.NOT_STARTED;
    }

    private boolean isCancelled(TournamentJson json) {
        List<TournamentJson.GameJson> gameJsons = json.games();
        GameStatus first = GameStatus.fromCode(gameJsons.get(0).statusType());
        GameStatus second = GameStatus.fromCode(gameJsons.get(1).statusType());
        return first == GameStatus.CANCELED && second == GameStatus.CANCELED;
    }

    private boolean isFinished(TournamentJson json) {
        List<TournamentJson.GameJson> gameJsons = json.games();
        for (TournamentJson.GameJson gameJson : gameJsons) {
            GameStage stage = GameStage.fromCode(gameJson.groupType());
            if (stage != GameStage.FINAL) continue;
            GameStatus status = GameStatus.fromCode(gameJson.statusType());
            boolean fin = status == GameStatus.COMPLETED || status == GameStatus.CANCELED;
            if (fin) log.debug("FINISHED: финал statusType={}", status);
            return fin;
        }
        return false;
    }

    /**
     * Турнир начался, если есть goes ИЛИ хотя бы один матч с непустым счётом/сетами
     */
    private boolean isInProgress(TournamentJson json) {
        List<TournamentJson.GameJson> gameJsons = json.games();
        for (TournamentJson.GameJson gameJson : gameJsons) {
            GameStatus status = GameStatus.fromCode(gameJson.statusType());
            if (status == GameStatus.GOES) {
                log.debug("IN_PROGRESS: матч идёт — gameId={}", gameJson.gameId());
                return true;
            }
        }
        return false;
    }

}