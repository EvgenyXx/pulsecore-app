package ru.pulsecore.app.shop.api.controller.shop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.request.CreateOrderRequest;
import ru.pulsecore.app.shop.api.dto.response.OrderDto;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;
import ru.pulsecore.app.shop.application.order.OrderUserService;

import java.util.List;

@Tag(name = "Shop — Orders", description = "Заказы")
@RestController
@RequestMapping(ShopApi.BASE_PATH)
@RequiredArgsConstructor
public class OrderController {

    private final OrderUserService orderUserService;

    @Operation(summary = "Мои активные заказы (CONFIRMED, ASSEMBLED)")
    @GetMapping(ShopApi.ORDERS_ACTIVE)
    public ResponseEntity<List<OrderDto>> getMyActiveOrders(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(orderUserService.getUserActiveOrders(principal.playerId()));
    }

    @Operation(summary = "Создать заказ")
    @PostMapping(ShopApi.ORDERS)
    public ResponseEntity<OrderDto> createOrder(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderUserService.createOrder(principal.playerId(), request));
    }

    @Operation(summary = "Мои заказы")
    @GetMapping(ShopApi.ORDERS)
    public ResponseEntity<Page<OrderDto>> getMyOrders(
            @CurrentPlayer PlayerPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(orderUserService.getUserOrders(principal.playerId(), page, size));
    }

    @Operation(summary = "Заказ по ID")
    @GetMapping(ShopApi.ORDER)
    public ResponseEntity<OrderDto> getOrder(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderUserService.getUserOrder(principal.playerId(), orderId));
    }
}