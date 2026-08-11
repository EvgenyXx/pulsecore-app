package ru.pulsecore.app.admin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.client.PaymentClient;
import ru.pulsecore.app.shared.dto.response.PricesResponse;
import ru.pulsecore.app.admin.api.dto.request.UpdatePricesRequest;

@Tag(name = "Admin", description = "Управление ценами подписки")
@RequiredArgsConstructor
@AdminController
public class AdminPriceController {

    private final PaymentClient paymentClient;

    @Operation(summary = "Обновить цены подписки")
    @PutMapping(AdminApi.UPDATE_SUB_PRICE)
    public ResponseEntity<PricesResponse> updatePrices(@Valid @RequestBody UpdatePricesRequest request) {
        return ResponseEntity.ok(paymentClient.updatePrices(
                request.getOneMonth(),
                request.getTwoMonths()
        ));
    }

    @Operation(summary = "Получить текущие цены подписки")
    @GetMapping(AdminApi.GET_PRICE_SUB)
    public ResponseEntity<PricesResponse> getSubPrice() {
        return ResponseEntity.ok(paymentClient.getPrices());
    }
}