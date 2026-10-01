package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.*;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductCardDto;
import ru.pulsecore.app.shop.api.dto.response.ProductCreateResponse;
import ru.pulsecore.app.shop.api.dto.response.ProductDetailDto;
import ru.pulsecore.app.shop.domain.Product;
import ru.pulsecore.app.shop.domain.ProductImage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ===== card (список/каталог) =====

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "mainImageUrl", expression = "java(resolveMainImageUrl(product))")
    @Mapping(target = "images", expression = "java(resolveImageUrls(product))")
    ProductCardDto toCardDto(Product product);

    List<ProductCardDto> toCardDtoList(List<Product> products);

    // ===== detail (детальная/редактирование) =====

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "images", expression = "java(resolveImageDtos(product))")
    ProductDetailDto toDetailDto(Product product);

    // ===== entity → DTO (create response) =====

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "mainImageUrl", expression = "java(resolveMainImageUrl(product))")
    @Mapping(target = "images", expression = "java(resolveImageUrls(product))")
    ProductCreateResponse toDto(Product product);

    List<ProductCreateResponse> toDtoList(List<Product> products);

    // ===== request → entity =====

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequest request);

    // ===== helpers =====

    default String resolveMainImageUrl(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) return null;
        return product.getImages().stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElseGet(() -> product.getImages().get(0).getUrl());
    }

    default List<String> resolveImageUrls(Product product) {
        if (product.getImages() == null) return List.of();
        return product.getImages().stream()
                .map(ProductImage::getUrl)
                .toList();
    }

    default List<ProductDetailDto.ImageDto> resolveImageDtos(Product product) {
        if (product.getImages() == null) return List.of();
        return product.getImages().stream()
                .map(img -> new ProductDetailDto.ImageDto(img.getId(), img.getUrl()))
                .toList();
    }
}