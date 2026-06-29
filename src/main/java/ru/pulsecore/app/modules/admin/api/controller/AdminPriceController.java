package ru.pulsecore.app.modules.admin.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.modules.admin.api.AdminApi;
import ru.pulsecore.app.modules.admin.api.dto.PricesResponse;
import ru.pulsecore.app.modules.admin.api.dto.UpdatePricesRequest;
import ru.pulsecore.app.modules.payment.service.PriceService;


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