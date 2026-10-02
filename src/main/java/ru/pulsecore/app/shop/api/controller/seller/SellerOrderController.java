package ru.pulsecore.app.shop.api.controller.seller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.SellerApi;
import ru.pulsecore.app.shop.api.dto.request.UpdateOrderStatusRequest;
import ru.pulsecore.app.shop.api.dto.request.UpdatePaymentStatusRequest;
import ru.pulsecore.app.shop.api.dto.response.SellerOrderDto;
import ru.pulsecore.app.shop.application.order.OrderSellerService;
import ru.pulsecore.app.shop.domain.OrderStatus;

import java.util.List;

@Tag(name = "Seller — Orders", description = "Управление заказами")
@RestController
@RequestMapping(SellerApi.BASE_PATH)
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerOrderController {

    private final OrderSellerService orderSellerService;

    @Operation(summary = "Список заказов")
    @GetMapping(SellerApi.ORDERS)
    public ResponseEntity<List<SellerOrderDto>> getAll(
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderSellerService.getAllOrders(status));
    }

    @Operation(summary = "Заказ по ID")
    @GetMapping(SellerApi.ORDER)
    public ResponseEntity<SellerOrderDto> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderSellerService.getOrder(orderId));
    }

    @Operation(summary = "Изменить статус заказа")
    @PatchMapping(SellerApi.ORDER_STATUS)
    public ResponseEntity<SellerOrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderSellerService.updateOrderStatus(orderId, request.status()));
    }

    @Operation(summary = "Изменить статус оплаты")
    @PatchMapping(SellerApi.ORDER_PAYMENT_STATUS)
    public ResponseEntity<SellerOrderDto> updatePaymentStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdatePaymentStatusRequest paymentStatusRequest) {
        return ResponseEntity.ok(orderSellerService.updatePaymentStatus(orderId, paymentStatusRequest.paymentStatus()));
    }
}