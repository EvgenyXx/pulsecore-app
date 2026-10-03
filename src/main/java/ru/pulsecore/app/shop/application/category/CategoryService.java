package ru.pulsecore.app.shop.application.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.response.CategoryDto;
import ru.pulsecore.app.shop.api.dto.request.CreateCategoryRequest;
import ru.pulsecore.app.shop.application.mapping.CategoryMapper;
import ru.pulsecore.app.shop.domain.entity.Category;
import ru.pulsecore.app.shop.infrastructure.repository.CategoryRepository;
import ru.pulsecore.app.shop.infrastructure.exception.CategoryNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Transactional
    public CategoryDto create(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.name())
                .active(true)
                .build();

        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Transactional
    public void delete(Long categoryId) {
       Category category = getCategoryById(categoryId);
       categoryRepository.delete(category);
       log.info("Категория {} удалена",category.getName());
    }
}