package ru.pulsecore.app.payment.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.shared.dto.response.PricesResponse;
import ru.pulsecore.app.payment.api.PaymentApi;
import ru.pulsecore.app.payment.application.PriceService;

@Tag(name = "Payment", description = "Цены подписки")
@RestController
@RequestMapping(PaymentApi.BASE_PATH)
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @Operation(summary = "Получить цены подписки")
    @GetMapping(PaymentApi.PRICES)
    public ResponseEntity<PricesResponse> getPrices() {
        return ResponseEntity.ok(priceService.getPricesData());
    }
}