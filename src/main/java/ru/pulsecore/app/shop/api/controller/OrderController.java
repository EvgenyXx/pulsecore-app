package ru.pulsecore.app.shop.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.request.CreateOrderRequest;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shop.application.order.OrderService;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.List;

@Tag(name = "Shop — Orders", description = "Заказы")
@RestController
@RequestMapping(ShopApi.BASE_PATH + ShopApi.ORDERS)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Создать заказ")
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(principal.playerId(), request));
    }

    @Operation(summary = "Мои заказы")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getMyOrders(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(orderService.getUserOrders(principal.playerId()));
    }

    @Operation(summary = "Заказ по ID")
    @GetMapping(ShopApi.ORDER)
    public ResponseEntity<OrderDto> getOrder(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getUserOrder(principal.playerId(), orderId));
    }
}