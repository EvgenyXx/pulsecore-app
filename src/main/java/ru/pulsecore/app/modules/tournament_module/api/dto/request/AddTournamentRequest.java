package ru.pulsecore.app.modules.tournament_module.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTournamentRequest {
    private String url;
}