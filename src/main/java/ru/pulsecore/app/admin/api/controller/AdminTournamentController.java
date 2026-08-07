package ru.pulsecore.app.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.client.TournamentClient;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.dto.response.AdminCalculateResponse;

import java.util.Map;
import java.util.UUID;

@AdminController
@RequiredArgsConstructor
public class AdminTournamentController {

    private final TournamentClient tournamentClient;

    //todo сделать дто  не принимать мапу ...
    @PostMapping(AdminApi.TOURNAMENT_CALCULATE)
    public ResponseEntity<AdminCalculateResponse> calculate(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String startDate = request.get("startDate");
        String endDate = request.get("endDate");

        if (name == null || startDate == null || endDate == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tournamentClient.calculate(name, startDate, endDate));
    }


    @DeleteMapping(AdminApi.PLAYER_TOURNAMENTS)
    public ResponseEntity<MessageResponse> deletePlayerTournaments(@PathVariable UUID id) {
      return ResponseEntity.ok(tournamentClient.deleteAllTournaments(id));
    }


    @PostMapping(AdminApi.PLAYER_TOURNAMENTS_RESYNC)
    public ResponseEntity<MessageResponse> resyncPlayerTournaments(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentClient.resyncAll(id));
    }
}