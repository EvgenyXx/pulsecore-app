package ru.pulsecore.app.modules.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(PriceApi.BASE)
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping(PriceApi.PRICES)
    public ResponseEntity<Map<Integer, Integer>> getPrices() {
        return ResponseEntity.ok(priceService.getPrices());
    }

    @PutMapping(PriceApi.ADMIN_PRICES)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePrices(@RequestBody Map<String, Integer> body) {
        int price1 = body.getOrDefault("1", priceService.getPrice(1));
        int price2 = body.getOrDefault("2", priceService.getPrice(2));
        priceService.update(price1, price2);
        return ResponseEntity.ok(Map.of("status", "ok", "prices", priceService.getPrices()));
    }
}