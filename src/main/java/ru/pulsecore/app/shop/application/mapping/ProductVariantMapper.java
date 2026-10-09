package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pulsecore.app.shop.api.dto.request.ProductVariantRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductVariantDto;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductSize;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductVariantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "color", ignore = true)   // устанавливается в сервисе
    @Mapping(target = "size", ignore = true)    // устанавливается в сервисе
    @Mapping(target = "stock", source = "request.stock")
    @Mapping(target = "priceDelta", source = "request.priceDelta")
    ProductVariant toEntity(ProductVariantRequest request);

    @Mapping(target = "colorId", source = "color.id")
    @Mapping(target = "color", source = "color.color")
    @Mapping(target = "sizeId", source = "size.id")
    @Mapping(target = "size", source = "size.size")
    ProductVariantDto toDto(ProductVariant variant);

    List<ProductVariantDto> toDtoList(List<ProductVariant> variants);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "stock", source = "request.stock")
    @Mapping(target = "priceDelta", source = "request.priceDelta")
    void update(ProductVariantRequest request, @MappingTarget ProductVariant variant);

    /**
     * Хелпер — привязать color/size к варианту.
     * Вызывается из сервиса после создания ProductColor и ProductSize.
     */
    default ProductVariant bind(ProductVariant variant, ProductColor color, ProductSize size) {
        variant.setColor(color);
        variant.setSize(size);
        return variant;
    }
}