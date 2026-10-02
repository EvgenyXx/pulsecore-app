package ru.pulsecore.app.shop.api.controller.shop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shop.api.ShopApi;
import ru.pulsecore.app.shop.api.dto.response.CategoryDto;
import ru.pulsecore.app.shop.application.category.CategoryService;

import java.util.List;

@Tag(name = "Shop — Categories", description = "Категории")
@RestController
@RequestMapping(ShopApi.BASE_PATH)
@RequiredArgsConstructor
public class ShopCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Список категорий")
    @GetMapping(ShopApi.CATEGORIES)
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }


}