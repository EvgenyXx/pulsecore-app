package ru.pulsecore.app.tournament.application.sum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.api.dto.response.SumResponse;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SumFacade {

    private final SumService sumService;



    public SumResponse getSum(UUID id, LocalDate start, LocalDate end, int page, int size) {
        return sumService.getSum(id, start, end, page, size);
    }

    public void updateResult(Long id, Double amount, Double bonus) {
        sumService.updateResult(id, amount, bonus);
    }

}
