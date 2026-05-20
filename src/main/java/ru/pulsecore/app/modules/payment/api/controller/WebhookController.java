package ru.pulsecore.app.modules.payment.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.player.service.subscription.WebhookService;


import java.util.Map;

import static ru.pulsecore.app.modules.player.api.PlayerApi.WEBHOOK;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping(WEBHOOK)
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> body) {
        log.info("Webhook received");
        webhookService.process(body);
        return ResponseEntity.ok("ok");
    }
}