package ru.pulsecore.app.tournament.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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


}