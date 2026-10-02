package ru.pulsecore.app.shop.api.controller.seller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.SellerApi;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.request.CreateCategoryRequest;
import ru.pulsecore.app.shop.api.dto.response.CategoryDto;
import ru.pulsecore.app.shop.application.category.CategoryService;

import java.util.List;

@Tag(name = "Seller — Categories", description = "Управление категориями")
@RestController
@RequestMapping(SellerApi.BASE_PATH)
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Список категорий")
    @GetMapping(SellerApi.CATEGORIES)
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @Operation(summary = "Создать категорию")
    @PostMapping(SellerApi.CATEGORIES)
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @DeleteMapping(SellerApi.CATEGORY)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.noContent().build();
    }

}