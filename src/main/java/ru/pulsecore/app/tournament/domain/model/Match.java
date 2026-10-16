package ru.pulsecore.app.tournament.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pulsecore.app.tournament.domain.enums.MatchStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    private String stage;
    private String player1;
    private String player2;
    private int score1;
    private int score2;
    private String setsDetails;
    private String league;
    private String table;
    private String status;       // statusTitle (человекочитаемый)
    private String statusType;   // statusType (машинный: "canceled" / "completed")
    private String groupType;
    private int sortNumber;

    public MatchStatus getStatusEnum() {
        return MatchStatus.fromCode(statusType);
    }

    public boolean isCanceled() {
        return getStatusEnum() == MatchStatus.CANCELED;
    }

    public boolean isCompleted() {
        return getStatusEnum() == MatchStatus.COMPLETED;
    }

    public String winnerOf() {
        if (score1 > score2) return player1;
        return player2;
    }

    public String loserOf() {
        if (score1 > score2) return player2;
        return player1;
    }

    public Match reverse() {
        Match m = new Match();
        m.setPlayer1(this.player2);
        m.setPlayer2(this.player1);
        m.setScore1(this.score2);
        m.setScore2(this.score1);
        m.setStage(this.stage);
        m.setLeague(this.league);
        m.setTable(this.table);
        m.setStatus(this.status);
        m.setStatusType(this.statusType);
        m.setGroupType(this.groupType);
        m.setSortNumber(this.sortNumber);
        return m;
    }
}