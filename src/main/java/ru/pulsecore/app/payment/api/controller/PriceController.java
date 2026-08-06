package ru.pulsecore.app.payment.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.shared.dto.response.PricesResponse;
import ru.pulsecore.app.payment.api.PaymentApi;
import ru.pulsecore.app.payment.application.PriceService;



@RestController
@RequestMapping(PaymentApi.BASE_PATH)
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping(PaymentApi.PRICES)
    public ResponseEntity<PricesResponse> getPrices() {
        return ResponseEntity.ok(priceService.getPricesData());
    }


}