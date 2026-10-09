package ru.pulsecore.app.shop.api.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "Название обязательно")
        @Size(max = 200, message = "Название не должно превышать 200 символов")
        String name,

        @Size(max = 5000, message = "Описание слишком длинное")
        String description,

        @NotNull(message = "Категория обязательна")
        Long categoryId,

        @Size(max = 100, message = "Название бренда слишком длинное")
        String brand,

        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.00", message = "Цена не может быть отрицательной")
        @Digits(integer = 8, fraction = 2, message = "Неверный формат цены")
        BigDecimal price,

        List<ProductColorRequest> colors,

        List<ProductSizeRequest> sizes,

        List<ProductVariantRequest> variants
) {}