package ru.pulsecore.app.shop.api.controller.shop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Operation(summary = "Поиск товаров по названию или бренду")
    @GetMapping(ShopApi.PRODUCTS_SEARCH)
    public ResponseEntity<Page<ProductCardDto>> search(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(productService.search(query, page, size));
    }

    @Operation(summary = "Товары по категории (с пагинацией)")
    @GetMapping(ShopApi.PRODUCTS_BY_CATEGORY)
    public ResponseEntity<Page<ProductCardDto>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(productService.getByCategory(categoryId, page, size));
    }

    @Operation(summary = "Список товаров (активные)")
    @GetMapping(ShopApi.PRODUCTS)
    public ResponseEntity<Page<ProductCardDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(productService.getAllActive(page, size));
    }

    @Operation(summary = "Товар по ID")
    @GetMapping(ShopApi.PRODUCT)
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}