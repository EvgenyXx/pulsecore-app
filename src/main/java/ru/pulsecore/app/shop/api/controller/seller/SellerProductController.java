package ru.pulsecore.app.shop.api.controller.seller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.SellerApi;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductUpdateRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductCreateResponse;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.application.image.ProductImageService;
import ru.pulsecore.app.shop.application.product.ProductService;

import java.util.List;

@Tag(name = "Seller — Products", description = "Управление товарами")
@RestController
@RequestMapping(SellerApi.BASE_PATH)
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    @Operation(summary = "Список всех товаров")
    @GetMapping(SellerApi.PRODUCTS)
    public ResponseEntity<List<ProductCardDto>> getAll(
            @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(productService.getByCategory(categoryId));
        }
        return ResponseEntity.ok(productService.getAllActive());
    }

    @Operation(summary = "Товар по ID")
    @GetMapping(SellerApi.PRODUCT)
    public ResponseEntity<ProductDetailDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Operation(summary = "Создать товар")
    @PostMapping(SellerApi.PRODUCTS)
    public ResponseEntity<ProductCreateResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Operation(summary = "Обновить товар")
    @PatchMapping(SellerApi.PRODUCT)
    public ResponseEntity<ProductDetailDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @Operation(summary = "Удалить товар")
    @DeleteMapping(SellerApi.PRODUCT)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить фото товара")
    @DeleteMapping(SellerApi.PRODUCT_IMAGE)
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }
}