package ru.pulsecore.app.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TournamentDto {

    private Long id;
    private String title;
    private String hall;
    private String hall2; // ← ДОБАВЬ
    private String league;
    private String link;

    private DateDto date;

    private List<String> players;

    private String type;

    private Integer hallNumber;
}