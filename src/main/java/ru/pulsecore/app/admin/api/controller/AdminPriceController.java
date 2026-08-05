package ru.pulsecore.app.admin.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.infrastructure.clinet.PaymentClient;
import ru.pulsecore.app.shared.dto.PricesResponse;
import ru.pulsecore.app.admin.api.dto.request.UpdatePricesRequest;



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