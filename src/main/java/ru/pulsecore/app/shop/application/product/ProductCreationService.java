package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.CreateProductRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductColorRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductSizeRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductVariantRequest;
import ru.pulsecore.app.shop.application.category.CategoryService;
import ru.pulsecore.app.shop.application.mapping.ProductMapper;
import ru.pulsecore.app.shop.domain.entity.Category;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductSize;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;
import ru.pulsecore.app.shop.infrastructure.exception.ProductValidationException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductCreationService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductColorService productColorService;
    private final ProductSizeService productSizeService;
    private final ProductVariantService productVariantService;

    @Transactional
    public Product create(CreateProductRequest request) {
        Category category = categoryService.getCategoryById(request.categoryId());

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product = productRepository.save(product);

        Map<String, ProductColor> colorsByValue = createColors(product, request.colors());
        Map<String, ProductSize> sizesByValue = createSizes(product, request.sizes());
        List<ProductVariant> variants = createVariants(product, request.variants(), colorsByValue, sizesByValue);

        product.setVariants(variants);
        return product;
    }

    private Map<String, ProductColor> createColors(Product product, List<ProductColorRequest> requests) {
        Map<String, ProductColor> map = new HashMap<>();
        if (requests == null || requests.isEmpty()) return map;

        for (ProductColorRequest req : requests) {
            String key = req.color() == null ? "" : req.color().trim();
            if (map.containsKey(key)) {
                throw new ProductValidationException("Дубликат цвета: \"" + key + "\"");
            }
            map.put(key, productColorService.create(product, req));
        }

        return map;
    }

    private Map<String, ProductSize> createSizes(Product product, List<ProductSizeRequest> requests) {
        Map<String, ProductSize> map = new HashMap<>();
        if (requests == null || requests.isEmpty()) return map;

        for (ProductSizeRequest req : requests) {
            String key = req.size() == null ? "" : req.size().trim();
            if (map.containsKey(key)) {
                throw new ProductValidationException("Дубликат размера: \"" + key + "\"");
            }
            map.put(key, productSizeService.create(product, req));
        }

        return map;
    }

    private List<ProductVariant> createVariants(Product product,
                                                List<ProductVariantRequest> requests,
                                                Map<String, ProductColor> colorsByValue,
                                                Map<String, ProductSize> sizesByValue) {
        List<ProductVariant> result = new ArrayList<>();
        if (requests == null || requests.isEmpty()) return result;

        for (ProductVariantRequest req : requests) {
            String colorKey = req.color() == null ? "" : req.color().trim();
            String sizeKey  = req.size()  == null ? "" : req.size().trim();

            ProductColor color = colorsByValue.get(colorKey);
            ProductSize  size  = sizesByValue.get(sizeKey);

            if (color == null && req.color() != null) {
                throw new ProductValidationException("Неизвестный цвет у варианта: \"" + req.color() + "\"");
            }
            if (size == null && req.size() != null) {
                throw new ProductValidationException("Неизвестный размер у варианта: \"" + req.size() + "\"");
            }

            result.add(productVariantService.create(product, color, size, req));
        }

        return result;
    }
}