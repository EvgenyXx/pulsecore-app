package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.pulsecore.app.shop.api.dto.request.ProductColorRequest;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;

@Mapper(componentModel = "spring")
public interface ProductColorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "product")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "color", source = "request.color")
    @Mapping(target = "sortOrder", expression = "java(request.sortOrder() != null ? request.sortOrder() : 0)")
    @Mapping(target = "createdAt", ignore = true)
    ProductColor toEntity(ProductColorRequest request, Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "color", source = "color")
    @Mapping(target = "sortOrder", source = "sortOrder")
    @Mapping(target = "createdAt", ignore = true)
    void update(ProductColorRequest request, @MappingTarget ProductColor color);

}