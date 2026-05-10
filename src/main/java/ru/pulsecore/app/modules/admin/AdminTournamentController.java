package ru.pulsecore.app.modules.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.tournament.api.dto.AdminCalculateResponse;
import ru.pulsecore.app.modules.tournament.application.AdminCalculateService;

import java.util.Map;


@AdminController
@RequiredArgsConstructor
public class AdminTournamentController {

    private final AdminCalculateService adminCalculateService;

    @PostMapping(AdminApi.TOURNAMENT_CALCULATE)
    public ResponseEntity<AdminCalculateResponse> calculate(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String startDate = request.get("startDate");
        String endDate = request.get("endDate");

        if (name == null || startDate == null || endDate == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adminCalculateService.calculate(name, startDate, endDate));
    }
}