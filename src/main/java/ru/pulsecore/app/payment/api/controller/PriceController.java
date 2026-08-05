package ru.pulsecore.app.payment.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.shared.dto.response.PricesResponse;
import ru.pulsecore.app.payment.api.PriceApi;
import ru.pulsecore.app.payment.application.PriceService;



@RestController
@RequestMapping(PriceApi.BASE)
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping(PriceApi.PRICES)
    public ResponseEntity<PricesResponse> getPrices() {
        return ResponseEntity.ok(priceService.getPricesData());
    }


}