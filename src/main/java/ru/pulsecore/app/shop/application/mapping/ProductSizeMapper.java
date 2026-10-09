package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pulsecore.app.shop.api.dto.request.ProductSizeRequest;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductSize;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductSizeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "product")
    @Mapping(target = "size", source = "request.size")
    @Mapping(target = "sortOrder", expression = "java(request.sortOrder() != null ? request.sortOrder() : 0)")
    @Mapping(target = "createdAt", ignore = true)
    ProductSize toEntity(ProductSizeRequest request, Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "size", source = "size")
    @Mapping(target = "sortOrder", source = "sortOrder")
    @Mapping(target = "createdAt", ignore = true)
    void update(ProductSizeRequest request, @MappingTarget ProductSize size);
}