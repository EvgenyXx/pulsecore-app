package ru.pulsecore.app.shop.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductCreateResponse;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.application.image.ProductImageService;
import ru.pulsecore.app.shop.application.product.ProductService;

import java.util.List;

@Tag(name = "Shop", description = "Магазин")
@RestController
@RequestMapping(ShopApi.BASE_PATH)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @GetMapping(ShopApi.GET_PRODUCT)
    public ResponseEntity<ProductDetailDto>getProductById(@PathVariable Long productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Operation(summary = "Создать продукт")
    @PostMapping(ShopApi.CREATE_PRODUCT)
    public ResponseEntity<ProductCreateResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Operation(summary = "Список товаров")
    @GetMapping(ShopApi.PRODUCTS)
    public ResponseEntity<List<ProductCardDto>> getAll(
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(productService.getByCategory(categoryId));
        }
        return ResponseEntity.ok(productService.getAllActive());
    }

    @Operation(summary = "Удалить фото товара")
    @DeleteMapping(ShopApi.PRODUCT_IMAGE)
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }

     @Operation(summary = "Удалить товар")
    @DeleteMapping(ShopApi.PRODUCT)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }
}