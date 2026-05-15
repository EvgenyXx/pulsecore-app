package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.payment.YookassaService;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.PaymentResponse;

import java.util.UUID;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PaymentController {

    private final YookassaService yookassaService;//1

    @PostMapping(PlayerApi.PAY)
    public ResponseEntity<PaymentResponse> pay(@PathVariable UUID id, @RequestParam int months) {
        var payment = yookassaService.createPayment(id, months);
        return ResponseEntity.ok(new PaymentResponse(payment.confirmationUrl()));
    }
}