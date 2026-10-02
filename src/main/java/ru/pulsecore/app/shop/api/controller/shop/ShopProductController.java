package ru.pulsecore.app.shop.api.controller.shop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.application.product.ProductService;

import java.util.List;

@Tag(name = "Shop — Products", description = "Каталог товаров")
@RestController
@RequestMapping(ShopApi.BASE_PATH)
@RequiredArgsConstructor
public class ShopProductController {

    private final ProductService productService;

    @Operation(summary = "Список товаров (активные)")
    @GetMapping(ShopApi.PRODUCTS)
    public ResponseEntity<List<ProductCardDto>> getAll() {
        return ResponseEntity.ok(productService.getAllActive());
    }

    @Operation(summary = "Товар по ID")
    @GetMapping(ShopApi.PRODUCT)
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}