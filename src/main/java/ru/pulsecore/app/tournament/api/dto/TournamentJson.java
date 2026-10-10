package ru.pulsecore.app.tournament.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TournamentJson(
        Long tourId,
        String title,
        String typeId,
        String country,
        Boolean showFntr,
        Boolean showSchema,
        Boolean showGroup,
        String schemaModClass,
        String gamesSectionClass,
        String gamesSectionBg,
        String date,
        String time,
        String leagueTitle,
        String hallTitle,
        List<PlayerJson> players,
        List<GameJson> games,
        List<Object> group,
        StreamJson stream,
        LabelsJson labels
) {

    // ===== Players =====

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PlayerJson(
            Long id,
            Long playerId,
            String name,
            String url,
            String thumbnail,
            @JsonProperty("ratings_ml") Integer ratingsMl,
            @JsonProperty("ratings_fntr") Integer ratingsFntr,
            Integer position,
            Boolean removed,
            Integer gamesAll,
            Integer gamesWin,
            Integer gamesLoose,
            String gamesLabel,
            Integer rating,
            Integer delta,
            String deltaClass
    ) {}

    // ===== Games =====

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GameJson(
            Long gameId,
            Integer sortNumber,
            String link,
            String groupType,
            String groupTitle,
            String statusType,
            String statusTitle,
            String time,
            String hallTitle,
            List<Integer> score,
            String setsStr,
            Boolean showScore,
            GamePlayerJson player1,
            GamePlayerJson player2
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GamePlayerJson(
            Long id,
            Long playerId,
            String name,
            String url,
            String thumbnail,
            @JsonProperty("ratings_ml") Object ratingsMl,   // может быть Integer или String ("")
            @JsonProperty("ratings_fntr") Object ratingsFntr,
            Integer result,
            String className,
            Boolean removed,
            Boolean isPlaceholder
    ) {}

    // ===== Stream =====

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StreamJson(
            String mode,
            String thumbnail,
            String streamsportId,
            String zalid,
            Long tourId
    ) {}

    // ===== Labels =====

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LabelsJson(
            String date,
            String time,
            String league,
            String hall,
            String toursPlayers,
            String player,
            String games,
            String ratingFntr,
            String ratingMl,
            String deltaTour,
            String meetingTour,
            String gamesTour,
            String round,
            String match,
            String status,
            String player1,
            String player2,
            String score,
            String group,
            String rating,
            String sets,
            String goals,
            String points,
            String place,
            String hallShort,
            String statusShort,
            String close,
            String loading,
            String loadError,
            String details
    ) {}
}