package ru.pulsecore.app.tournament.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.pulsecore.app.tournament.domain.enums.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TournamentContext {



    private Long tournamentId;
    private TournamentStatus tournamentStatus;
    private String date;
    private List<Match> matches;
    private LeagueType league;
    private double nightBonus;
    private RemovedStage removedStage;
    private String removedPlayer;
    private String time;
    private String typeId;
    private WithdrawalStage withdrawalStage;





    /**
     * Версия турнира, вычисляется из typeId.
     * Не хранится как поле, чтобы не было рассинхрона с typeId.
     */
    public TournamentVersion getVersion() {
        return TournamentVersion.fromTypeId(typeId)
                .orElse(TournamentVersion.STANDARD);
    }

}