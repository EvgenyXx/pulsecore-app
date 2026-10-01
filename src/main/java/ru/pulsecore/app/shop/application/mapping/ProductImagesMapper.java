package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.domain.ProductImage;

@Mapper(componentModel = "spring")
public interface ProductImagesMapper {

    ProductImage toEntity(CreateProductRequest.ImageRequest request);
}
