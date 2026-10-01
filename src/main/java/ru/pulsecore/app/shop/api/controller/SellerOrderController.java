package ru.pulsecore.app.shop.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.SellerApi;
import ru.pulsecore.app.shop.api.dto.request.UpdateOrderStatusRequest;
import ru.pulsecore.app.shop.api.dto.response.SellerOrderDto;
import ru.pulsecore.app.shop.application.order.OrderService;
import ru.pulsecore.app.shop.domain.OrderStatus;
import java.util.List;

@Tag(name = "Seller — Orders", description = "Управление заказами")
@RestController
@RequestMapping(SellerApi.BASE_PATH+ SellerApi.ORDERS)
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerOrderController {

    private final OrderService orderService;

    @Operation(summary = "Список заказов (с фильтром по статусу)")
    @GetMapping
    public ResponseEntity<List<SellerOrderDto>> getAll(
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getAllOrders(status));
    }

    @Operation(summary = "Заказ по ID")
    @GetMapping(SellerApi.ORDER)
    public ResponseEntity<SellerOrderDto> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @Operation(summary = "Изменить статус заказа")
    @PatchMapping(SellerApi.ORDER_STATUS)
    public ResponseEntity<SellerOrderDto> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.status()));
    }
}