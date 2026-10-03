package ru.pulsecore.app.shop.application.mapping;

import org.mapstruct.Mapper;

import ru.pulsecore.app.shop.api.dto.response.CategoryDto;
import ru.pulsecore.app.shop.domain.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);
}