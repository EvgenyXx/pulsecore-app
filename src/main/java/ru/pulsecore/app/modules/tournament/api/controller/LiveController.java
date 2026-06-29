package ru.pulsecore.app.modules.tournament.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.tournament.api.TournamentApi;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentLiveDto;
import ru.pulsecore.app.modules.tournament.service.LiveService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class LiveController {

    private final LiveService liveService;

    @GetMapping(TournamentApi.LIVE)
    public ResponseEntity<List<TournamentLiveDto>> getLive() {
        return ResponseEntity.ok(liveService.getLive());
    }

    @GetMapping(TournamentApi.ONLINE_ALL)
    public ResponseEntity<Map<Long, Long>> getAllOnline() {
        return ResponseEntity.ok(liveService.getOnlineCounts());
    }
}