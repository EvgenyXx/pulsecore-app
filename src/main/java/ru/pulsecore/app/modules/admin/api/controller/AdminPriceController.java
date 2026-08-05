package ru.pulsecore.app.modules.admin.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.modules.admin.api.AdminApi;
import ru.pulsecore.app.modules.admin.infrastructure.clinet.PaymentClient;
import ru.pulsecore.app.modules.shared.dto.PricesResponse;
import ru.pulsecore.app.modules.admin.api.dto.request.UpdatePricesRequest;



@RequiredArgsConstructor
@AdminController
public class AdminPriceController {

    private final PaymentClient  paymentClient;


    @PutMapping(AdminApi.PRICES)
    public ResponseEntity<PricesResponse> updatePrices(@Valid @RequestBody UpdatePricesRequest request) {
       return ResponseEntity.ok(paymentClient.updatePrices(
               request.getOneMonth(),
               request.getTwoMonths()
       ));
    }
}