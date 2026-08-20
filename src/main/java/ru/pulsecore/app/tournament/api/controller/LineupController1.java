package ru.pulsecore.app.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.LineupRepository;


// todo перенести в TournamentApi + LineupController или переделать на сервис
/**
 * Отдает состав по ID. Используется на странице лайв-трансляции.
 * Перенести в LineupController (TournamentApi.BASE_PATH + "/{id}").
 */
@RestController
@RequiredArgsConstructor
public class LineupController1 {

    private final LineupRepository lineupRepository;

    @GetMapping("/api/lineups/{id}")
    public Lineup getLineup(@PathVariable Long id) {
        return lineupRepository.findById(id).orElse(null);
    }
}