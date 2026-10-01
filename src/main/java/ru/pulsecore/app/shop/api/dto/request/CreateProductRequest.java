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
        @DecimalMin(value = "0.00", inclusive = true, message = "Цена не может быть отрицательной")
        @Digits(integer = 8, fraction = 2, message = "Неверный формат цены")
        BigDecimal price,

        @NotNull(message = "Количество обязательно")
        @Min(value = 0, message = "Количество не может быть отрицательным")
        Integer stock,

        List<ImageRequest> images
) {
    public record ImageRequest(
            @NotBlank(message = "URL изображения обязателен")
            @Size(max = 500, message = "URL слишком длинный")
            String url,

            @Min(value = 0, message = "Порядок не может быть отрицательным")
            Integer sortOrder,

            boolean main
    ) {}
}