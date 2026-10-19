package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaryH2HPlayerDto {
    private String name;
    private String league;
    private Long tournamentsCount;
    private Double totalAmount;
    private Double avgAmount;
    private Double winratePercent;
    private Double topsisScore;
}