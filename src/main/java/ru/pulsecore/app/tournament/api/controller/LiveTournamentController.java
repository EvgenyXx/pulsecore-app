package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.TournamentLiveDto;
import ru.pulsecore.app.tournament.application.lineup.LiveTournamentService;
import java.util.List;
import java.util.Map;

@Tag(name = "Tournament", description = "Лайв-трансляции и зрители онлайн")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class LiveTournamentController {

    private final LiveTournamentService liveService;

    @Operation(summary = "Получить текущие матчи на сегодня со статусами")
    @GetMapping(TournamentApi.LIVE)
    public ResponseEntity<List<TournamentLiveDto>> getLive() {
        return ResponseEntity.ok(liveService.getLive());
    }

    @Operation(summary = "Количество зрителей онлайн по всем турнирам")
    @GetMapping(TournamentApi.ONLINE_ALL)
    public ResponseEntity<Map<Long, Long>> getAllOnline() {
        return ResponseEntity.ok(liveService.getOnlineCounts());
    }
}