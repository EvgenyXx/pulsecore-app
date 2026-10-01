package ru.pulsecore.app.shop.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.request.AddCartItemRequest;
import ru.pulsecore.app.shop.api.dto.request.UpdateCartItemRequest;
import ru.pulsecore.app.shop.api.dto.response.CartDto;
import ru.pulsecore.app.shop.application.cart.CartService;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

@Tag(name = "Shop — Cart", description = "Корзина")
@RestController
@RequestMapping(ShopApi.BASE_PATH + ShopApi.CART)
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Получить корзину текущего игрока")
    @GetMapping
    public ResponseEntity<CartDto> getCart(@CurrentPlayer PlayerPrincipal principal) {
        return ResponseEntity.ok(cartService.getOrCreate(principal.playerId()));
    }

    @Operation(summary = "Добавить товар в корзину")
    @PostMapping(ShopApi.CART_ITEMS)
    public ResponseEntity<CartDto> addItem(
            @CurrentPlayer PlayerPrincipal principal,
            @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.playerId(), request));
    }

    @Operation(summary = "Изменить количество товара")
    @PatchMapping(ShopApi.CART_ITEM)
    public ResponseEntity<CartDto> updateItem(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(principal.playerId(), itemId, request.quantity()));
    }

    @Operation(summary = "Удалить товар из корзины")
    @DeleteMapping(ShopApi.CART_ITEM)
    public ResponseEntity<Void> removeItem(
            @CurrentPlayer PlayerPrincipal principal,
            @PathVariable Long itemId) {
        cartService.removeItem(principal.playerId(), itemId);
        return ResponseEntity.noContent().build();
    }
}