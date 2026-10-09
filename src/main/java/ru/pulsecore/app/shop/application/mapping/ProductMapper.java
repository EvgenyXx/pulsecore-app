package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.*;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductUpdateRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductColorDto;
import ru.pulsecore.app.shop.api.dto.response.ProductCreateResponse;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.api.dto.response.ProductSizeDto;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductImage;
import ru.pulsecore.app.shop.domain.entity.ProductSize;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;

import java.util.List;
import java.util.Objects;

@Mapper(
        componentModel = "spring",
        uses = {ProductVariantMapper.class}
)
public interface ProductMapper {

    // ===== update =====

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProduct(ProductUpdateRequest request, @MappingTarget Product product);

    // ===== request → entity =====

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequest request);

    // ===== card =====

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "mainImageUrl", expression = "java(resolveMainImageUrl(product))")
    @Mapping(target = "images", expression = "java(resolveImageUrls(product))")
    @Mapping(target = "inStock", expression = "java(isInStock(product))")
    ProductCardDto toCardDto(Product product);

    // ===== detail =====

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "colors", expression = "java(resolveColors(product))")
    @Mapping(target = "sizes", expression = "java(resolveSizes(product))")
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "inStock", expression = "java(isInStock(product))")
    ProductDetailDto toDetailDto(Product product);

    // ===== create response =====

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "mainImageUrl", expression = "java(resolveMainImageUrl(product))")
    @Mapping(target = "images", expression = "java(resolveImageUrls(product))")
    @Mapping(target = "colors", expression = "java(resolveColors(product))")
    @Mapping(target = "sizes", expression = "java(resolveSizes(product))")
    @Mapping(target = "variants", source = "variants")
    ProductCreateResponse toDto(Product product);

    // ===== helpers =====

    default boolean isInStock(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) return false;
        return product.getVariants().stream()
                .anyMatch(v -> v.getStock() != null && v.getStock() > 0);
    }

    default List<ProductColor> uniqueColors(Product product) {
        if (product.getVariants() == null) return List.of();
        return product.getVariants().stream()
                .map(ProductVariant::getColor)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    default List<ProductSize> uniqueSizes(Product product) {
        if (product.getVariants() == null) return List.of();
        return product.getVariants().stream()
                .map(ProductVariant::getSize)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    default List<ProductColorDto> resolveColors(Product product) {
        return uniqueColors(product).stream()
                .map(this::toColorDto)
                .toList();
    }

    default List<ProductSizeDto> resolveSizes(Product product) {
        return uniqueSizes(product).stream()
                .map(s -> new ProductSizeDto(s.getId(), s.getSize(), s.getSortOrder()))
                .toList();
    }

    default ProductColorDto toColorDto(ProductColor color) {
        List<ProductColorDto.ImageDto> imgs = color.getImages() == null
                ? List.of()
                : color.getImages().stream()
                    .map(img -> new ProductColorDto.ImageDto(
                            img.getId(), img.getUrl(), img.getSortOrder(), img.isMain()))
                    .toList();
        return new ProductColorDto(color.getId(), color.getColor(), color.getSortOrder(), imgs);
    }

    default String resolveMainImageUrl(Product product) {
        List<ProductColor> colors = uniqueColors(product);
        if (colors.isEmpty()) return null;

        ProductColor preferred = colors.stream()
                .filter(this::hasImages)
                .filter(c -> colorHasStock(product, c))
                .findFirst()
                .orElse(null);

        if (preferred == null) {
            preferred = colors.stream()
                    .filter(this::hasImages)
                    .findFirst()
                    .orElse(null);
        }

        if (preferred == null) return null;
        return firstImageUrl(preferred);
    }

    default List<String> resolveImageUrls(Product product) {
        return uniqueColors(product).stream()
                .filter(this::hasImages)
                .flatMap(c -> c.getImages().stream())
                .map(ProductImage::getUrl)
                .distinct()
                .toList();
    }

    default boolean hasImages(ProductColor color) {
        return color != null && color.getImages() != null && !color.getImages().isEmpty();
    }

    default String firstImageUrl(ProductColor color) {
        if (!hasImages(color)) return null;
        return color.getImages().stream()
                .filter(ProductImage::isMain)
                .findFirst()
                .orElse(color.getImages().get(0))
                .getUrl();
    }

    default boolean colorHasStock(Product product, ProductColor color) {
        if (product.getVariants() == null) return false;
        return product.getVariants().stream()
                .anyMatch(v -> v.getColor() != null
                        && v.getColor().getId() != null
                        && v.getColor().getId().equals(color.getId())
                        && v.getStock() != null
                        && v.getStock() > 0);
    }
}