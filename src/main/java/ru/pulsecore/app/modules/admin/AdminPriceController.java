package ru.pulsecore.app.modules.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.payment.PriceService;

import java.util.Map;


@RequiredArgsConstructor
@AdminController
public class AdminPriceController {

    private final PriceService priceService;

    @PutMapping(AdminApi.PRICES)
    public ResponseEntity<Map<String, Object>> updatePrices(@RequestBody Map<String, Integer> body) {
        int price1 = body.getOrDefault("1", priceService.getPrice(1));
        int price2 = body.getOrDefault("2", priceService.getPrice(2));
        priceService.update(price1, price2);
        return ResponseEntity.ok(Map.of("status", "ok", "prices", priceService.getPrices()));
    }
}