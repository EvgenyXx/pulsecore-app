package ru.pulsecore.app.payment.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.payment.api.PaymentApi;
import ru.pulsecore.app.payment.api.dto.YookassaWebhook;
import ru.pulsecore.app.payment.application.handler.WebhookDispatcher;
import static ru.pulsecore.app.payment.api.PaymentApi.WEBHOOK;

@Tag(name = "Payment", description = "Вебхук оплаты")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(PaymentApi.BASE_PATH)
public class WebhookController {

    private final WebhookDispatcher dispatcher;

    @PostMapping(WEBHOOK)
    public ResponseEntity<String> handleWebhook(@RequestBody YookassaWebhook webhook) {
        dispatcher.dispatch(webhook);
        return ResponseEntity.ok().build();
    }
}