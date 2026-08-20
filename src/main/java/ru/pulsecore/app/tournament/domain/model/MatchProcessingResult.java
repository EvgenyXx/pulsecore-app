package ru.pulsecore.app.tournament.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MatchProcessingResult {

    private Map<String, Integer> pointsMap;
    private Map<String, Integer> placeMap;
}