package ru.pulsecore.app.shop.application.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shop.api.dto.request.ProductColorRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductSizeRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductUpdateRequest;
import ru.pulsecore.app.shop.api.dto.request.ProductVariantRequest;
import ru.pulsecore.app.shop.application.category.CategoryService;
import ru.pulsecore.app.shop.application.mapping.ProductMapper;
import ru.pulsecore.app.shop.domain.entity.Product;
import ru.pulsecore.app.shop.domain.entity.ProductColor;
import ru.pulsecore.app.shop.domain.entity.ProductSize;
import ru.pulsecore.app.shop.domain.entity.ProductVariant;
import ru.pulsecore.app.shop.infrastructure.exception.ProductValidationException;
import ru.pulsecore.app.shop.infrastructure.repository.ProductColorRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductSizeRepository;
import ru.pulsecore.app.shop.infrastructure.repository.ProductVariantRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductUpdateService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductColorService productColorService;
    private final ProductSizeService productSizeService;
    private final ProductVariantService productVariantService;
    private final ProductColorRepository productColorRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    public Product update(Product product, ProductUpdateRequest request) {
        productMapper.updateProduct(request, product);

        if (request.categoryId() != null) {
            product.setCategory(categoryService.getCategoryById(request.categoryId()));
        }

        Map<String, ProductColor> colorsByValue = syncColors(product, request.colors());
        Map<String, ProductSize> sizesByValue = syncSizes(product, request.sizes());
        syncVariants(product, request.variants(), colorsByValue, sizesByValue);

        return productRepository.save(product);
    }

    private Map<String, ProductColor> syncColors(Product product, List<ProductColorRequest> requests) {
        List<ProductColor> existing = productColorRepository.findByProductId(product.getId());
        Map<String, ProductColor> existingByValue = new HashMap<>();
        existing.forEach(c -> existingByValue.put(valueOf(c.getColor()), c));

        if (requests == null) return existingByValue;

        Map<String, ProductColor> result = new HashMap<>();

        for (ProductColorRequest req : requests) {
            String key = valueOf(req.color());
            ProductColor color = existingByValue.get(key);

            if (color != null) {
                productColorService.update(color, req);
            } else {
                color = productColorService.create(product, req);
            }
            result.put(key, color);
        }

        return result;
    }

    private Map<String, ProductSize> syncSizes(Product product, List<ProductSizeRequest> requests) {
        List<ProductSize> existing = productSizeRepository.findByProductId(product.getId());
        Map<String, ProductSize> existingByValue = new HashMap<>();
        existing.forEach(s -> existingByValue.put(valueOf(s.getSize()), s));

        if (requests == null) return existingByValue;

        Map<String, ProductSize> result = new HashMap<>();

        for (ProductSizeRequest req : requests) {
            String key = valueOf(req.size());
            ProductSize size = existingByValue.get(key);

            if (size != null) {
                productSizeService.update(size, req);
            } else {
                size = productSizeService.create(product, req);
            }
            result.put(key, size);
        }

        return result;
    }

    private void syncVariants(Product product,
                              List<ProductVariantRequest> requests,
                              Map<String, ProductColor> colorsByValue,
                              Map<String, ProductSize> sizesByValue) {

        if (requests == null) return;

        List<ProductVariant> existing = productVariantRepository.findByProductId(product.getId());
        Map<String, ProductVariant> existingByKey = new HashMap<>();
        existing.forEach(v -> existingByKey.put(keyOf(v), v));

        List<ProductVariant> result = new ArrayList<>();

        for (ProductVariantRequest req : requests) {
            ProductColor color = colorsByValue.get(valueOf(req.color()));
            ProductSize  size  = sizesByValue.get(valueOf(req.size()));

            validateResolved(color, size, req);

            result.add(upsertVariant(product, color, size, req, existingByKey));
        }

        product.setVariants(result);
    }

    private void validateResolved(ProductColor color, ProductSize size, ProductVariantRequest req) {
        if (color == null && req.color() != null) {
            throw new ProductValidationException("Неизвестный цвет у варианта: \"" + req.color() + "\"");
        }
        if (size == null && req.size() != null) {
            throw new ProductValidationException("Неизвестный размер у варианта: \"" + req.size() + "\"");
        }
    }

    private ProductVariant upsertVariant(Product product,
                                         ProductColor color,
                                         ProductSize size,
                                         ProductVariantRequest req,
                                         Map<String, ProductVariant> existingByKey) {
        String key = keyOf(color, size);
        ProductVariant variant = existingByKey.get(key);

        if (variant != null) {
            productVariantService.update(variant, req);
            return variant;
        }
        return productVariantService.create(product, color, size, req);
    }

    private String valueOf(String value) {
        return value == null ? "" : value.trim();
    }

    private String keyOf(ProductVariant variant) {
        return keyOf(variant.getColor(), variant.getSize());
    }

    private String keyOf(ProductColor color, ProductSize size) {
        String c = color == null ? "" : String.valueOf(color.getId());
        String s = size == null ? "" : String.valueOf(size.getId());
        return c + "|" + s;
    }
}