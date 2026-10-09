package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.shop.api.dto.request.ProductColorRequest;
import ru.pulsecore.app.shop.api.dto.response.ProductColorDto;
import ru.pulsecore.app.shop.domain.entity.ProductImage;

@Mapper(componentModel = "spring")
public interface ProductImagesMapper {

    /**
     * ProductColorRequest.ImageRequest → ProductImage.
     * color устанавливается в сервисе.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "url", source = "url")
    @Mapping(target = "sortOrder", source = "sortOrder")
    @Mapping(target = "main", source = "main")
    ProductImage toEntity(ProductColorRequest.ImageRequest request);

    /**
     * ProductImage → ProductColorDto.ImageDto.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "sortOrder", source = "sortOrder")
    @Mapping(target = "main", source = "main")
    ProductColorDto.ImageDto toDto(ProductImage image);
}