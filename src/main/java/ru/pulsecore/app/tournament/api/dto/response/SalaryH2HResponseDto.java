package ru.pulsecore.app.tournament.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaryH2HResponseDto {
    private SalaryH2HPlayerDto player1;
    private SalaryH2HPlayerDto player2;
    private String winnerName;
    private String period;
}