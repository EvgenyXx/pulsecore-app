package ru.pulsecore.app.modules.player_modeles.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.payment_modules.application.PaymentFacade;
import ru.pulsecore.app.modules.player_modeles.api.PlayerApi;
import ru.pulsecore.app.modules.player_modeles.api.dto.response.PaymentResponse;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

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