package ru.pulsecore.app.modules.lineup.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.entity.Lineup;
import ru.pulsecore.app.modules.tournament_module.infrastructure.persistence.repository.LineupRepository;


@RestController
@RequiredArgsConstructor
public class LineupController1 {

    private final LineupRepository lineupRepository;

    @GetMapping("/api/lineups/{id}")
    public Lineup getLineup(@PathVariable Long id) {
        return lineupRepository.findById(id).orElse(null);
    }
}