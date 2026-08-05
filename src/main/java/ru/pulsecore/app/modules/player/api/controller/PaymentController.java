package ru.pulsecore.app.modules.player.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.payment.application.PaymentFacade;
import ru.pulsecore.app.modules.player.api.PlayerApi;
import ru.pulsecore.app.modules.player.api.dto.response.PaymentResponse;
import ru.pulsecore.app.modules.shared.security.CurrentPlayer;
import ru.pulsecore.app.modules.shared.security.PlayerPrincipal;

@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping(PlayerApi.PAY)
    public ResponseEntity<PaymentResponse> pay(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam int months) {
        return ResponseEntity.ok(paymentFacade.createPayment(principal.playerId(), months));
    }
}