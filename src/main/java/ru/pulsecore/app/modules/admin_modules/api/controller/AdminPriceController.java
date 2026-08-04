package ru.pulsecore.app.modules.admin_modules.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.modules.admin_modules.api.AdminApi;
import ru.pulsecore.app.modules.admin_modules.api.dto.PricesResponse;
import ru.pulsecore.app.modules.admin_modules.api.dto.UpdatePricesRequest;
import ru.pulsecore.app.modules.payment_modules.application.PriceService;


@RequiredArgsConstructor
@AdminController
public class AdminPriceController {

    private final PriceService priceService;


    @PutMapping(AdminApi.PRICES)
    public ResponseEntity<PricesResponse> updatePrices(@Valid @RequestBody UpdatePricesRequest request) {
        priceService.update(request.getOneMonth(), request.getTwoMonths());
        return ResponseEntity.ok(priceService.getPrices());
    }
}