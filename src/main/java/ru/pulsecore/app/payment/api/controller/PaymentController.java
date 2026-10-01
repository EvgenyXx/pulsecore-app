package ru.pulsecore.app.payment.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.payment.api.PaymentApi;
import ru.pulsecore.app.payment.application.SubscriptionPaymentService;
import ru.pulsecore.app.shared.dto.response.PaymentResponse;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

@Tag(name = "Payment", description = "Оплата подписки")
@RestController
@RequestMapping(PaymentApi.BASE_PATH)
@RequiredArgsConstructor
public class PaymentController {

    private final SubscriptionPaymentService subscriptionPaymentService;


    @Operation(summary = "Создать платеж")
    @PostMapping(PaymentApi.PAY)
    public ResponseEntity<PaymentResponse> pay(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam int months) {
        return ResponseEntity.ok(subscriptionPaymentService.createPayment(principal.playerId(), months));
    }
}